package dev.skosarev.accountservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.skosarev.accountservice.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_")
@JsonPropertyOrder({"id", "name", "lastname", "email"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String lastname;

    private String email;

    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> userGroups = new HashSet<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    @Column(name = "failed_attempt")
    private int failedAttempt = 0;

    public User() {
    }

    public User(UserDto dto) {
        this.email = dto.getEmail();
        this.name = dto.getName();
        this.lastname = dto.getLastname();
        this.password = dto.getPassword();
    }

    public void addGroup(Group group) {
        userGroups.add(group);
    }

    public void removeGroup(String role) {
        for (Group group : userGroups) {
            if (group.getName().equals(role)) {
                userGroups.remove(group);
                break;
            }
        }
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Group> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<Group> userGroups) {
        this.userGroups = userGroups;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    @Transient
    public String getUsername() {
        return email;
    }

    public int getFailedAttempt() {
        return failedAttempt;
    }

    public void setFailedAttempt(int failedAttempt) {
        this.failedAttempt = failedAttempt;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean containsRole(String roleName) {
        for (Group role : userGroups) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @JsonIgnore
    public List<GrantedAuthority> getAuthorities() {
        Set<Group> userGroups = getUserGroups();
        List<GrantedAuthority> authorities = new ArrayList<>(userGroups.size());
        for (Group userGroup : userGroups) {
            authorities.add(new SimpleGrantedAuthority(userGroup.getName()));
        }

        return authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userGroups=" + userGroups +
                ", accountNonLocked=" + accountNonLocked +
                ", failedAttempt=" + failedAttempt +
                '}';
    }
}