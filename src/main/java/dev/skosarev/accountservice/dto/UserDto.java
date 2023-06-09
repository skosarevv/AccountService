package dev.skosarev.accountservice.dto;

import dev.skosarev.accountservice.model.Group;
import dev.skosarev.accountservice.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonPropertyOrder({"id", "name", "lastname", "email", "roles"})
public class UserDto {

    @Min(1)
    private Long id;

    @NotBlank(message = "The name should not be empty")
    private String name;

    @NotBlank(message = "The lastname should not be empty")
    private String lastname;

    @NotBlank
    @Email
    @Pattern(regexp = ".+@acme\\.com", message = "Email should ends with @acme.com")
    private String email;

    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private List<Group> roles;

    public UserDto() {
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail().toLowerCase();
        this.password = user.getPassword();
        this.roles = new ArrayList<>(user.getUserGroups());
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty(value = "roles", access = JsonProperty.Access.READ_ONLY)
    public List<String> getRoles() {
        return roles.stream().map(Group::getName).sorted().collect(Collectors.toList());
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
