package com.example.assignment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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

    /**
     * Convert a string value to a Role enum.
     * @param value the string value to convert
     * @return the corresponding Role enum
     */
    @JsonCreator
    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }

    /**
     * Convert the Role enum to its string value.
     * @return the string value of the Role enum
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
