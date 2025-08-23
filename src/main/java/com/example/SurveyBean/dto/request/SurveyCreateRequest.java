package com.example.SurveyBean.dto.request;

import com.example.SurveyBean.domain.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyCreateRequest {

    private String title;
    private String description;
    private List<QuestionDto> questions;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {
        private String text;
        private QuestionType type;
        private List<ChoiceDto> choices;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChoiceDto {
        private String text;
    }
}
