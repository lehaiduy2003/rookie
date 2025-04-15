package com.example.assignment.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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

    /**
     * Convert a string value to a ProductPriority enum.
     * @param value the string value to convert
     * @return the corresponding ProductPriority enum
     */
    @JsonCreator
    public static ProductPriority fromValue(String value) {
        for (ProductPriority priority : ProductPriority.values()) {
            if (priority.name().equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid product priority: " + value);
    }
    /**
     * Convert the ProductPriority enum to its string value.
     * @return the string value of the ProductPriority enum
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
