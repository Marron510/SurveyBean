package com.example.SurveyBean.dto.response;

import com.example.SurveyBean.domain.Question;
import com.example.SurveyBean.domain.QuestionType;
import com.example.SurveyBean.domain.Survey;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class SurveyResultResponse {

    private Long surveyId;
    private String title;
    private List<QuestionResultDto> questions;

    public static SurveyResultResponse from(Survey survey, Map<Long, List<String>> subjectiveAnswers, Map<Long, Map<Long, Long>> choiceCounts) {
        List<QuestionResultDto> questionResults = survey.getQuestions().stream()
                .map(question -> QuestionResultDto.from(question, subjectiveAnswers.get(question.getId()), choiceCounts.get(question.getId())))
                .toList();

        return SurveyResultResponse.builder()
                .surveyId(survey.getId())
                .title(survey.getTitle())
                .questions(questionResults)
                .build();
    }


    @Getter
    @Builder
    public static class QuestionResultDto {
        private Long questionId;
        private String text;
        private QuestionType type;
        private List<ChoiceResultDto> choices; // 객관식용
        private List<String> subjectiveAnswers; // 주관식용

        public static QuestionResultDto from(Question question, List<String> subjectiveAnswers, Map<Long, Long> choiceCounts) {
            List<ChoiceResultDto> choiceResults = null;
            if (question.getType() == QuestionType.MULTIPLE_CHOICE || question.getType() == QuestionType.SINGLE_CHOICE) {
                choiceResults = question.getChoices().stream()
                        .map(choice -> ChoiceResultDto.from(choice, choiceCounts.getOrDefault(choice.getId(), 0L)))
                        .toList();
            }

            return QuestionResultDto.builder()
                    .questionId(question.getId())
                    .text(question.getText())
                    .type(question.getType())
                    .choices(choiceResults)
                    .subjectiveAnswers(subjectiveAnswers)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ChoiceResultDto {
        private Long choiceId;
        private String text;
        private long count;

        public static ChoiceResultDto from(com.example.SurveyBean.domain.Choice choice, long count) {
            return ChoiceResultDto.builder()
                    .choiceId(choice.getId())
                    .text(choice.getText())
                    .count(count)
                    .build();
        }
    }
}
