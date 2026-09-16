package com.example.Study.Common;

public enum BookingStatusEnum {
    PENDING("false"),
    APPROVED("true"),
    REJECTED("rejected");

    private final String databaseValue;

    BookingStatusEnum(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String databaseValue() {
        return databaseValue;
    }

    public static BookingStatusEnum fromFilter(String value) {
        return switch (value == null ? "" : value.trim().toLowerCase()) {
            case "pending", "false" -> PENDING;
            case "approved", "true" -> APPROVED;
            case "rejected" -> REJECTED;
            default -> throw new IllegalArgumentException("Trạng thái lịch hẹn không hợp lệ");
        };
    }
}
