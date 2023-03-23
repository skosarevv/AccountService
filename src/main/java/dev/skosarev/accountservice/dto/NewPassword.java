package dev.skosarev.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class NewPassword {

    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @NotBlank
    @JsonProperty(value = "new_password")
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
