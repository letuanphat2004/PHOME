package com.example.Study.Common;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum RoleEnum {
    Tenant(1),
    Landlord(2),
    Admin(3);

    @Getter(onMethod_ = @JsonValue)
    private final Integer value;

    RoleEnum(Integer value) {
        this.value = value;
    }

    public static RoleEnum fromValue(Integer value) {
        return Stream.of(RoleEnum.values())
                .filter(targetEnum -> targetEnum.value.equals(value))
                .findFirst().orElse(null);
    }
}
