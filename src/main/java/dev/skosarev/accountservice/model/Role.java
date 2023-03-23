package dev.skosarev.accountservice.model;

import javax.persistence.Table;

@Table(name = "\"USER_ROLES\"")
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
