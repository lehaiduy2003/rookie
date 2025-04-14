package com.example.assignment.enums;


import lombok.Getter;

@Getter
public enum ProductPriority {
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low");

    private final String value;

    ProductPriority(String value) {
        this.value = value;
    }
}
