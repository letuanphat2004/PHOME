package com.example.Study.Common;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum RoomType {
    CHUNG_CHU(1),
    KHONG_CHUNG_CHU(2);
@Getter(onMethod_ = @JsonValue)
private final int value;
RoomType(int value) {
this.value = value;

}
public static RoomType fromValue(Integer value) {
return Stream.of(RoomType.values()).filter(r -> r.getValue() == value).findFirst().orElse(null);
}
}
