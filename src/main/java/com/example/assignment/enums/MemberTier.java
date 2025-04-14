package com.example.assignment.enums;

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
}
