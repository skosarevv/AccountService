package dev.skosarev.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class EditRoleDto {
    @JsonProperty("user")
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
