package dev.skosarev.accountservice.controller;

import dev.skosarev.accountservice.dto.NewPassword;
import dev.skosarev.accountservice.dto.UserDto;
import dev.skosarev.accountservice.model.User;
import dev.skosarev.accountservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("api/auth/")
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("signup")
    public ResponseEntity<UserDto> signup(@Valid @RequestBody UserDto userDto) {

        User user = new User(userDto);
        userService.register(user);
        UserDto resultUserDto = new UserDto(user);

        logger.info("Sign up: {}", userDto.getEmail());
        return ResponseEntity.ok(resultUserDto);
    }

    @PostMapping("changepass")
    public ResponseEntity<Map<String, String>> changePass(@Valid @RequestBody NewPassword newPassword, @AuthenticationPrincipal UserDetails details) {
        logger.info("Change password: {}", details.getUsername());
        return ResponseEntity.ok(userService.changePassword((User) details, newPassword.getNewPassword()));
    }
}
