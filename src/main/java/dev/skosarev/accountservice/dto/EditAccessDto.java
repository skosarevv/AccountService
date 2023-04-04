package dev.skosarev.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class EditAccessDto {
    @JsonProperty("user")
    @Email
    private String email;

    @NotBlank
    private String operation;

    public String getEmail() {
        return email;
    }

    public String getOperation() {
        return operation;
    }
}
