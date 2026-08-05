package com.portai.domain.jobposting.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

/**
 * required_skills / preferred_skills 같은 JSON 배열 컬럼을 List<String>으로 매핑하기 위한 컨버터.
 * (JSON 전용 라이브러리를 추가하지 않고 Jackson만으로 직렬화/역직렬화)
 */
@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("스킬 목록을 JSON으로 변환하는 데 실패했습니다.", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(dbData, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("스킬 목록 JSON을 파싱하는 데 실패했습니다.", e);
        }
    }
}
