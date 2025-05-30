package com.example.Study.Common;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum BookingStatusEnum {
    Confirm(1),
    Cancel(2);

    @Getter(onMethod_ = @JsonValue)
    private final Integer value;

    BookingStatusEnum(Integer value) {
        this.value = value;
    }

    public static BookingStatusEnum fromValue(Integer value) {
        return Stream.of(BookingStatusEnum.values())
                .filter(targetEnum -> targetEnum.value.equals(value))
                .findFirst().orElse(null);
    }
}
