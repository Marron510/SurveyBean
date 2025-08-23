package com.example.SurveyBean.dto.response;

import com.example.SurveyBean.domain.Choice;
import com.example.SurveyBean.domain.Question;
import com.example.SurveyBean.domain.QuestionType;
import com.example.SurveyBean.domain.Survey;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SurveyResponse {

    private final Long surveyId;
    private final String title;
    private final String description;
    private final LocalDateTime createdAt;
    private final List<QuestionDto> questions;

    @Builder
    private SurveyResponse(Long surveyId, String title, String description, LocalDateTime createdAt, List<QuestionDto> questions) {
        this.surveyId = surveyId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.questions = questions;
    }

    public static SurveyResponse from(Survey survey) {
        return SurveyResponse.builder()
                .surveyId(survey.getId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createdAt(survey.getCreatedAt())
                .questions(survey.getQuestions().stream()
                        .map(QuestionDto::from)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    public static class QuestionDto {
        private final Long questionId;
        private final String text;
        private final QuestionType type;
        private final List<ChoiceDto> choices;

        @Builder
        private QuestionDto(Long questionId, String text, QuestionType type, List<ChoiceDto> choices) {
            this.questionId = questionId;
            this.text = text;
            this.type = type;
            this.choices = choices;
        }

        public static QuestionDto from(Question question) {
            return QuestionDto.builder()
                    .questionId(question.getId())
                    .text(question.getText())
                    .type(question.getType())
                    .choices(question.getChoices().stream()
                            .map(ChoiceDto::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    public static class ChoiceDto {
        private final Long choiceId;
        private final String text;

        @Builder
        private ChoiceDto(Long choiceId, String text) {
            this.choiceId = choiceId;
            this.text = text;
        }

        public static ChoiceDto from(Choice choice) {
            return ChoiceDto.builder()
                    .choiceId(choice.getId())
                    .text(choice.getText())
                    .build();
        }
    }
}
