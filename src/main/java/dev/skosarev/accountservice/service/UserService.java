package dev.skosarev.accountservice.service;

import dev.skosarev.accountservice.dto.EditRoleDto;
import dev.skosarev.accountservice.dto.UserDto;
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

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;
    private final String[] breachedPasswords = {"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};

    @Autowired
    public UserService(UserRepository userRepository, GroupRepository groupRepository, PasswordEncoder passwordEncoder) {
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

        return Map.of(
                "email", user.getEmail().toLowerCase(),
                "status", "The password has been updated successfully");
    }

    public UserDto changeRoles(EditRoleDto editRoleDto) {
        String role = editRoleDto.getRole();
        String operation = editRoleDto.getOperation();
        if (role.equals("ADMINISTRATOR") && operation.equals("REMOVE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        if (!(role.equals("ADMINISTRATOR") || role.equals("ACCOUNTANT") || role.equals("USER"))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }

        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(editRoleDto.getEmail());
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        User user = userOptional.get();
        if (operation.equals("REMOVE") && !user.containsRole("ROLE_" + role)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user does not have a role!");
        }
        if (user.getUserGroups().size() == 1 && operation.equals("REMOVE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        }
        if (operation.equals("GRANT") && isCombiningBusinessAndAdministrativeRoles(user, "ROLE_" + role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }

        if (operation.equals("GRANT") && !user.containsRole("ROLE_" + role)) {
            user.addGroup(groupRepository.findByName("ROLE_" + role));
        }
        if (operation.equals("REMOVE")) {
            user.removeGroup("ROLE_" + role);
        }

        userRepository.save(user);
        return new UserDto(user);
    }

    private boolean isCombiningBusinessAndAdministrativeRoles(User user, String newRole) {
        if (newRole.equals("ROLE_ADMINISTRATOR") &&
                (user.containsRole("ROLE_ACCOUNTANT") || user.containsRole("ROLE_USER"))) {
            return true;
        }
        return (newRole.equals("ROLE_USER") || newRole.equals("ROLE_ACCOUNTANT")) &&
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

    public Map<String, String> deleteUserByEmail(String email) {
        Optional<User> optionalUserToDelete = userRepository.findByEmailIgnoreCase(email);
        if (optionalUserToDelete.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        User userToDelete = optionalUserToDelete.get();
        if (userToDelete.containsRole("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        userRepository.delete(userToDelete);
        return Map.of("user", email,
                "status", "Deleted successfully!");
    }
}
