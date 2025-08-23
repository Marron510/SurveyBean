package com.example.SurveyBean.service;

import com.example.SurveyBean.domain.*;
import com.example.SurveyBean.dto.request.AnswerSubmitRequest;
import com.example.SurveyBean.dto.request.SurveyCreateRequest;
import com.example.SurveyBean.dto.response.SurveyResponse;
import com.example.SurveyBean.dto.response.SurveyResultResponse;
import com.example.SurveyBean.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final AnswerRepository answerRepository;


    @Transactional
    public void createSurvey(SurveyCreateRequest request, String username) { // 컨트롤러에서 사용자 이름이 전달된다고 가정.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Survey survey = Survey.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        request.getQuestions().forEach(qDto -> {
            Question question = Question.builder()
                    .text(qDto.getText())
                    .type(qDto.getType())
                    .build();

            if ((qDto.getType() == QuestionType.MULTIPLE_CHOICE || qDto.getType() == QuestionType.SINGLE_CHOICE) && qDto.getChoices() != null) {
                qDto.getChoices().forEach(cDto -> {
                    Choice choice = Choice.builder()
                            .text(cDto.getText())
                            .build();
                    question.addChoice(choice);
                });
            }
            survey.addQuestion(question);
        });

        surveyRepository.save(survey);
    }

    public List<SurveyResponse> getSurveys() {
        return surveyRepository.findAll().stream()
                .map(SurveyResponse::from)
                .collect(Collectors.toList());
    }

    public SurveyResponse getSurveyById(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found with id: " + surveyId));
        return SurveyResponse.from(survey);
    }

    @Transactional
    public void submitAnswers(Long surveyId, AnswerSubmitRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        request.getAnswers().forEach(answerDto -> {
            Question question = questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("Question not found"));

            if (!question.getSurvey().getId().equals(surveyId)) {
                throw new IllegalStateException("Question does not belong to the specified survey.");
            }

            Choice choice = null;
            if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                if (answerDto.getChoiceId() == null) {
                    throw new IllegalArgumentException("Choice ID is required for multiple choice questions.");
                }
                choice = choiceRepository.findById(answerDto.getChoiceId())
                        .orElseThrow(() -> new IllegalArgumentException("Choice not found"));
                if (!choice.getQuestion().getId().equals(question.getId())) {
                    throw new IllegalStateException("Choice does not belong to the specified question.");
                }
            }

            Answer answer = Answer.builder()
                    .user(user)
                    .question(question)
                    .choice(choice)
                    .content(answerDto.getContent())
                    .build();

            answerRepository.save(answer);
        });
    }

    public SurveyResultResponse getSurveyResults(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found with id: " + surveyId));

        List<Long> questionIds = survey.getQuestions().stream().map(Question::getId).toList();
        List<Answer> answers = answerRepository.findByQuestionIdIn(questionIds);

        Map<Long, List<String>> subjectiveAnswers = new HashMap<>();
        Map<Long, Map<Long, Long>> choiceCounts = new HashMap<>();

        for (Question question : survey.getQuestions()) {
            if (question.getType() == QuestionType.TEXT) {
                List<String> contentList = answers.stream()
                        .filter(a -> a.getQuestion().getId().equals(question.getId()))
                        .map(Answer::getContent)
                        .filter(Objects::nonNull)
                        .toList();
                subjectiveAnswers.put(question.getId(), contentList);
            } else {
                Map<Long, Long> counts = answers.stream()
                        .filter(a -> a.getQuestion().getId().equals(question.getId()) && a.getChoice() != null)
                        .collect(Collectors.groupingBy(a -> a.getChoice().getId(), Collectors.counting()));
                choiceCounts.put(question.getId(), counts);
            }
        }

        return SurveyResultResponse.from(survey, subjectiveAnswers, choiceCounts);
    }
}
