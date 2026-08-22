package com.portai.domain.generation.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portai.domain.generation.dto.RecordIds;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * generations.record_ids JSON 컬럼과 RecordIds 객체를 매핑하기 위한 컨버터.
 * (JobPosting의 StringListJsonConverter와 동일한 방식, 대상 타입만 다름)
 */
@Converter
public class RecordIdsJsonConverter implements AttributeConverter<RecordIds, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(RecordIds attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("record_ids를 JSON으로 변환하는 데 실패했습니다.", e);
        }
    }

    @Override
    public RecordIds convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(dbData, RecordIds.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("record_ids JSON을 파싱하는 데 실패했습니다.", e);
        }
    }
}