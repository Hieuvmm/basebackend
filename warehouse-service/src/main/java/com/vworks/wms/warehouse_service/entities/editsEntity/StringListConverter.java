package com.vworks.wms.warehouse_service.entities.editsEntity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        return (list != null && !list.isEmpty()) ? String.join(SPLIT_CHAR, list) : "";
    }

    @Override
    public List<String> convertToEntityAttribute(String joined) {
        return (joined != null && !joined.isBlank())
                ? new ArrayList<>(Arrays.asList(joined.split(SPLIT_CHAR)))
                : new ArrayList<>();
    }
}