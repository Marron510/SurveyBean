package com.example.SurveyBean.service;

import com.example.SurveyBean.domain.*;
import com.example.SurveyBean.dto.request.AnswerSubmitRequest;
import com.example.SurveyBean.dto.request.SurveyCreateRequest;
import com.example.SurveyBean.dto.response.SurveyResponse;
import com.example.SurveyBean.dto.response.SurveyResultResponse;
import com.example.SurveyBean.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public void createSurvey(SurveyCreateRequest request, String username) {
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("질문을 생성해주십시오");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다.")); // 변경

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

    public List<SurveyResponse> getTop3Surveys() {
        Pageable top3 = PageRequest.of(0, 3);
        return surveyRepository.findTop3ByOrderByAnswersDesc(top3).stream()
                .map(SurveyResponse::from)
                .collect(Collectors.toList());
    }

    public List<SurveyResponse> getSurveysByUsername(String username) {
        return surveyRepository.findByUser_Username(username).stream()
                .map(SurveyResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SurveyResponse getSurveyById(Long surveyId) {
        Survey survey = surveyRepository.findByIdWithQuestions(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 설문지를 찾을 수 없습니다: " + surveyId)); // 변경
        survey.getQuestions().forEach(question -> Hibernate.initialize(question.getChoices()));
        return SurveyResponse.from(survey);
    }

    @Transactional
    public void submitAnswers(Long surveyId, AnswerSubmitRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다.")); // 변경

        request.getAnswers().forEach(answerDto -> {
            Question question = questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다.")); // 변경

            if (!question.getSurvey().getId().equals(surveyId)) {
                throw new IllegalStateException("해당 질문은 지정된 설문지에 속해있지 않습니다."); // 변경
            }

            Choice choice = null;
            if (question.getType() == QuestionType.MULTIPLE_CHOICE || question.getType() == QuestionType.SINGLE_CHOICE) {
                if (answerDto.getChoiceId() == null) {
                    throw new IllegalArgumentException("선택형 질문에는 선택지 ID가 필수입니다."); // 변경
                }
                choice = choiceRepository.findById(answerDto.getChoiceId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 선택지입니다.")); // 변경
                if (!choice.getQuestion().getId().equals(question.getId())) {
                    throw new IllegalStateException("해당 선택지는 지정된 질문에 속해있지 않습니다."); // 변경
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
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 설문지를 찾을 수 없습니다: " + surveyId)); // 변경

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

    @Transactional
    public void deleteSurvey(Long surveyId, String username) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 설문지를 찾을 수 없습니다: " + surveyId)); // 변경

        if (!survey.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("해당 설문지를 삭제할 권한이 없습니다."); // 변경
        }

        surveyRepository.delete(survey);
    }
}