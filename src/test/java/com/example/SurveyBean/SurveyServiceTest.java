package com.example.SurveyBean;

import com.example.SurveyBean.domain.Question;
import com.example.SurveyBean.domain.Survey;
import com.example.SurveyBean.domain.User;
import com.example.SurveyBean.domain.Survey;
import com.example.SurveyBean.domain.User;
import com.example.SurveyBean.dto.request.SurveyCreateRequest;
import com.example.SurveyBean.repository.SurveyRepository;
import com.example.SurveyBean.repository.UserRepository;
import com.example.SurveyBean.service.SurveyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SurveyService surveyService;

    @Test
    @DisplayName("설문 생성 - 성공 테스트")
    void createSurvey_success() {
        // given
        SurveyCreateRequest.QuestionDto questionDto = new SurveyCreateRequest.QuestionDto("Question 1", null, null);
        SurveyCreateRequest request = new SurveyCreateRequest("New Survey", "Survey Description", Collections.singletonList(questionDto));

        String username = "testuser";
        User user = User.builder()
                .username(username)
                .email("test@example.com")
                .password("password")
                .roles("ROLE_USER")
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when
        surveyService.createSurvey(request, username);

        // then
        verify(surveyRepository).save(any(Survey.class));
    }
}
