package dev.skosarev.accountservice.controller;

import dev.skosarev.accountservice.dto.EditRoleDto;
import dev.skosarev.accountservice.dto.UserDto;
import dev.skosarev.accountservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<UserDto>> getUser() {
        return ResponseEntity.ok(userService.getAllDto());
    }

    @DeleteMapping("user/{email}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String email) {
        return ResponseEntity.ok(userService.deleteUserByEmail(email));
    }

    @PutMapping("user/role")
    public ResponseEntity<UserDto> updateUserRole(@Valid @RequestBody EditRoleDto editRoleDto) {
        return ResponseEntity.ok(userService.changeRoles(editRoleDto));
    }
}
