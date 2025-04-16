package com.example.assignment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MemberTier {
    COMMON("common"),
    PREMIUM("premium"),
    VIP("vip");

    private final String value;

    MemberTier(String value) {
        this.value = value;
    }

    /**
     * Convert a string value to a MemberTier enum.
     * @param value the string value to convert
     * @return the corresponding MemberTier enum
     */
    @JsonCreator
    public static MemberTier fromValue(String value) {
        for (MemberTier tier : MemberTier.values()) {
            if (tier.name().equalsIgnoreCase(value)) {
                return tier;
            }
        }
        throw new IllegalArgumentException("Invalid member tier: " + value);
    }

    /**
     * Convert the MemberTier enum to its string value.
     * @return the string value of the MemberTier enum
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
