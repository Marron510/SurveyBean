package com.example.SurveyBean.controller;

import com.example.SurveyBean.dto.auth.LoginRequest;
import com.example.SurveyBean.dto.auth.SignupRequest;
import com.example.SurveyBean.dto.response.SurveyResponse;
import com.example.SurveyBean.dto.response.SurveyResultResponse;
import com.example.SurveyBean.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final SurveyService surveyService;

    @GetMapping("/")
    public String index(Model model) {
        List<SurveyResponse> surveys = surveyService.getSurveys();
        model.addAttribute("surveys", surveys);
        return "index"; // 이는 /resources/templates/index.html을 참조.
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping("/surveys/{id}")
    public String showSurveyDetail(@PathVariable Long id, Model model) {
        try {
            SurveyResponse survey = surveyService.getSurveyById(id);
            model.addAttribute("survey", survey);
            return "survey-detail";
        } catch (IllegalArgumentException e) {
            // 서비스에서 설문을 찾지 못해 예외가 발생한 경우 사용자 정의 404 에러 페이지로 이동
            return "error/404"; // 예: /resources/templates/error/404.html
        }
    }

    @GetMapping("/create-survey")
    public String showCreateSurveyForm() {
        return "create-survey";
    }

    @GetMapping("/surveys/{id}/results")
    public String showSurveyResults(@PathVariable Long id, Model model) {
        SurveyResultResponse results = surveyService.getSurveyResults(id);
        model.addAttribute("results", results);
        return "survey-results";
    }
}
