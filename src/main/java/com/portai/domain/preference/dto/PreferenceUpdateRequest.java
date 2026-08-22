package com.portai.domain.preference.dto;

import com.portai.domain.preference.entity.Style;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PreferenceUpdateRequest {

    private List<String> keywords;
    private List<String> emphasizedTypes;
    private Style style;

}