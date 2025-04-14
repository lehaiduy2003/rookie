package com.example.assignment.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    DELIVERING("delivering"),
    COMPLETED("completed");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
