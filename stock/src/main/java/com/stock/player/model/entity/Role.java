package com.stock.player.model.entity;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER"),
    ADMIN(combine("ROLE_ADMIN", "ROLE_USER"));

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public static String combine(String... names) {
        return String.join(",", names);
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
