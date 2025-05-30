package com.example.Study.Common;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter(autoApply = true)
public class RoomTypeConverter implements AttributeConverter<RoomType, String> {
    @Override
    public String convertToDatabaseColumn(RoomType roomType) {
        return roomType != null ? roomType.name() : null;
    }

    @Override
    public RoomType convertToEntityAttribute(String dbData) {
        return dbData != null ? RoomType.valueOf(dbData) : null;
    }

    public static RoomType convertToEntityAttributeGlobal(String dbData) {
        return dbData != null ? RoomType.valueOf(dbData) : null;
    }
}
