package com.vworks.wms.warehouse_service.entities.editsEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class BadgeConverter implements AttributeConverter<Badge, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Badge badge) {
        try {
            return objectMapper.writeValueAsString(badge);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Badge to JSON string", e);
        }
    }

    @Override
    public Badge convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, Badge.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON string to Badge", e);
        }
    }
}
