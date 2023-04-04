package dev.skosarev.accountservice.controller;

import dev.skosarev.accountservice.dto.EditAccessDto;
import dev.skosarev.accountservice.dto.EditRoleDto;
import dev.skosarev.accountservice.dto.UserDto;
import dev.skosarev.accountservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin/")
public class AdminRestController {
    private final UserService userService;

    @Autowired
    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("user")
    public ResponseEntity<?> getUser() {
        List<UserDto> userDtos = userService.getAllDto();
        if (userDtos.size() == 1) {
            return ResponseEntity.ok(userDtos.get(0));
        }
        return ResponseEntity.ok(userDtos);
    }

    @DeleteMapping("user/{email}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String email,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.deleteUserByEmail(email, userDetails.getUsername()));
    }

    @PutMapping("user/role")
    public ResponseEntity<UserDto> updateUserRole(@Valid @RequestBody EditRoleDto editRoleDto,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.changeRoles(editRoleDto, userDetails.getUsername()));
    }

    @PutMapping("user/access")
    public ResponseEntity<Map<String, String>> updateAccess(@Valid @RequestBody EditAccessDto editAccessDto,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.changeAccess(editAccessDto, userDetails.getUsername()));
    }
}
