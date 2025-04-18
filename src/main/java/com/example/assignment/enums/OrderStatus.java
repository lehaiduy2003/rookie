package com.example.assignment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum OrderStatus {
    DELIVERING("delivering"),
    COMPLETED("completed");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    /**
     * Convert a string value to an OrderStatus enum.
     * @param value the string value to convert
     * @return the corresponding OrderStatus enum
     */
    @JsonCreator
    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + value);
    }

    /**
     * Convert the OrderStatus enum to its string value.
     * @return the string value of the OrderStatus enum
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
