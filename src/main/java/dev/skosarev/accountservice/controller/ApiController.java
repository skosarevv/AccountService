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
@RequestMapping("api/")
public class ApiController {
    Logger logger = LoggerFactory.getLogger(ApiController.class);
    private final UserService service;

    @Autowired
    public ApiController(UserService service) {
        this.service = service;
    }

    @PostMapping("auth/signup")
    public ResponseEntity<UserDto> signup(@Valid @RequestBody UserDto userDto) {

        User user = new User(userDto);
        service.signup(user);
        UserDto resultUserDto = new UserDto(user);

        logger.info("Sign up: {}", userDto.getEmail());
        return ResponseEntity.ok(resultUserDto);
    }

    @PostMapping("auth/changepass")
    public ResponseEntity<Map<String, String>> changePass(@Valid @RequestBody NewPassword newPassword, @AuthenticationPrincipal UserDetails details) {
        logger.info("Change password: {}", details.getUsername());
        return ResponseEntity.ok(service.changePassword((User) details, newPassword.getNewPassword()));
    }

    @GetMapping("empl/payment")
    public UserDto getPayments(@AuthenticationPrincipal UserDetails details) {
        logger.info("Get payments: {}", details.getUsername());
        return new UserDto((User) details);
    }
}
