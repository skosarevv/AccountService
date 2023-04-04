package dev.skosarev.accountservice.config;

import dev.skosarev.accountservice.service.LoginAttemptService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest request;


    public AuthenticationFailureListener(LoginAttemptService loginAttemptService, HttpServletRequest request) {
        this.loginAttemptService = loginAttemptService;
        this.request = request;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        final String username = event.getAuthentication().getName();
        if (username != null) {
            loginAttemptService.loginFailure(username, request.getRequestURI());
        }
    }
}
