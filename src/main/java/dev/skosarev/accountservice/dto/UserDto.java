package dev.skosarev.accountservice.dto;

import dev.skosarev.accountservice.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.*;

@JsonPropertyOrder({"id", "name", "lastname", "email"})
public class UserDto {
    @Min(1)
    private Long id;
    @NotBlank(message = "The name should not be empty")
    private String name;
    @NotBlank(message = "The lastname should not be empty")
    private String lastname;
    @NotBlank
    @Email
    @Pattern(regexp = ".+@acme.com", message = "Email should ends with @acme.com")
    private String email;
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @NotBlank
    private String password;

    public UserDto() {
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
