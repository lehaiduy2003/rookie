package com.example.assignment.enums;

import lombok.Getter;

@Getter
public enum Role {
    CUSTOMER("customer"),
    ADMIN("admin"),
    SHOP_OWNER("shopOwner");

    private final String value;

    Role(String value) {
        this.value = value;
    }
}
