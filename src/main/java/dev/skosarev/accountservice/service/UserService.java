package dev.skosarev.accountservice.service;

import dev.skosarev.accountservice.dto.EditAccessDto;
import dev.skosarev.accountservice.dto.EditRoleDto;
import dev.skosarev.accountservice.dto.UserDto;
import dev.skosarev.accountservice.model.SecurityAction;
import dev.skosarev.accountservice.model.User;
import dev.skosarev.accountservice.repository.GroupRepository;
import dev.skosarev.accountservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final LogService logService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;
    private final String[] breachedPasswords = {"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};

    @Autowired
    public UserService(LogService logService, UserRepository userRepository, GroupRepository groupRepository, PasswordEncoder passwordEncoder) {
        this.logService = logService;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        if (Arrays.asList(breachedPasswords).contains(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.addGroup(groupRepository.findByName(userRepository.count() == 0 ? "ROLE_ADMINISTRATOR" : "ROLE_USER"));

        userRepository.save(user);
        logService.addEvent(SecurityAction.CREATE_USER, "Anonymous", user.getEmail(), "/api/auth/signup");
    }

    public Map<String, String> changePassword(User user, String newPassword) {
        if (Arrays.asList(breachedPasswords).contains(newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logService.addEvent(SecurityAction.CHANGE_PASSWORD, user.getEmail(), user.getEmail(), "/api/auth/changepass");

        return Map.of(
                "email", user.getEmail().toLowerCase(),
                "status", "The password has been updated successfully");
    }

    public UserDto changeRoles(EditRoleDto editRoleDto, String adminEmail) {
        String role = editRoleDto.getRole();
        String operation = editRoleDto.getOperation();
        if (role.equals("ADMINISTRATOR") && operation.equals("REMOVE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        if (!(role.equals("ADMINISTRATOR") || role.equals("ACCOUNTANT") || role.equals("USER") || role.equals("AUDITOR"))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }

        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(editRoleDto.getEmail());
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        User user = userOptional.get();
        if (operation.equals("REMOVE") && !user.containsRole("ROLE_" + role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        }
        if (user.getUserGroups().size() == 1 && operation.equals("REMOVE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        }
        if (operation.equals("GRANT") && isCombiningBusinessAndAdministrativeRoles(user, "ROLE_" + role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }

        if (operation.equals("GRANT") && !user.containsRole("ROLE_" + role)) {
            user.addGroup(groupRepository.findByName("ROLE_" + role));
            logService.addEvent(SecurityAction.GRANT_ROLE, adminEmail,
                    String.format("Grant role %s to %s", role, editRoleDto.getEmail()), "/api/admin/user/role");
        }
        if (operation.equals("REMOVE")) {
            user.removeGroup("ROLE_" + role);
            logService.addEvent(SecurityAction.REMOVE_ROLE, adminEmail,
                    String.format("Remove role %s from %s", role, editRoleDto.getEmail()), "/api/admin/user/role");
        }

        userRepository.save(user);
        return new UserDto(user);
    }

    public Map<String, String> changeAccess(EditAccessDto editAccessDto, String adminEmail) {
        User user = userRepository.findByEmailIgnoreCase(editAccessDto.getEmail()).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        if (user.containsRole("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        }

        if (editAccessDto.getOperation().equals("LOCK")) {
            user.setAccountNonLocked(false);
            logService.addEvent(SecurityAction.LOCK_USER, adminEmail,
                    String.format("Lock user %s", editAccessDto.getEmail()), "/api/admin/user/access");
        } else {
            user.setAccountNonLocked(true);
            logService.addEvent(SecurityAction.UNLOCK_USER, adminEmail,
                    String.format("Unlock user %s", editAccessDto.getEmail()), "/api/admin/user/access");
        }

        userRepository.save(user);
        return Map.of("status", String.format("User %s %s!",
                editAccessDto.getEmail(), editAccessDto.getOperation().equals("LOCK") ? "locked" : "unlocked"));
    }

    private boolean isCombiningBusinessAndAdministrativeRoles(User user, String newRole) {
        if (newRole.equals("ROLE_ADMINISTRATOR") &&
                (user.containsRole("ROLE_ACCOUNTANT") ||
                        user.containsRole("ROLE_USER") ||
                        user.containsRole("ROLE_AUDITOR")
                )) {
            return true;
        }
        return (newRole.equals("ROLE_USER") || newRole.equals("ROLE_ACCOUNTANT") || newRole.equals("ROLE_AUDITOR")) &&
                user.containsRole("ROLE_ADMINISTRATOR");
    }

    public List<User> getAll() {
        return userRepository.findAllByOrderById();
    }

    public List<UserDto> getAllDto() {
        return getAll()
                .stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    public Map<String, String> deleteUserByEmail(String email, String adminEmail) {
        Optional<User> optionalUserToDelete = userRepository.findByEmailIgnoreCase(email);
        if (optionalUserToDelete.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        User userToDelete = optionalUserToDelete.get();
        if (userToDelete.containsRole("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        userRepository.delete(userToDelete);
        logService.addEvent(SecurityAction.DELETE_USER, adminEmail, email, "/api/admin/user");
        return Map.of("user", email,
                "status", "Deleted successfully!");
    }
}
