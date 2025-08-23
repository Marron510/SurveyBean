package com.example.SurveyBean.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AnswerSubmitRequest {

    private List<AnswerDto> answers;

    @Getter
    @NoArgsConstructor
    public static class AnswerDto {
        private Long questionId;
        private Long choiceId; // 객관식용
        private String content; // 주관식용
    }
}
