package dev.skosarev.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class EditRoleDto {
    @JsonProperty("user")
    @Pattern(regexp = ".+@acme\\.com", message = "Email should ends with @acme.com")
    @Email
    private String email;

    @NotBlank
    private String role;

    @NotBlank
    private String operation;

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getOperation() {
        return operation;
    }
}