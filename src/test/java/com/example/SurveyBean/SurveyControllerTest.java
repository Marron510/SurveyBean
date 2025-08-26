package com.example.SurveyBean;

import com.example.SurveyBean.dto.request.SurveyCreateRequest;
import com.example.SurveyBean.service.SurveyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SurveyService surveyService;

    @Test
    @DisplayName("설문 생성 API - 성공 테스트")
    @WithMockUser(username = "testuser", roles = "USER")
    void createSurvey_api_success() throws Exception {
        // given - 테스트를 위한 준비 단계
        SurveyCreateRequest.QuestionDto questionDto = new SurveyCreateRequest.QuestionDto("API Question 1", null, null);
        SurveyCreateRequest request = new SurveyCreateRequest("API Test Survey", "Survey created via API test", Collections.singletonList(questionDto));

        doNothing().when(surveyService).createSurvey(any(SurveyCreateRequest.class), eq("testuser"));

        // when
        mockMvc.perform(post("/api/surveys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
        // then
                .andExpect(status().isCreated());
    }
}
