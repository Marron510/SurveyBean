package com.example.SurveyBean.controller;

import com.example.SurveyBean.dto.request.AnswerSubmitRequest;
import com.example.SurveyBean.dto.request.SurveyCreateRequest;
import com.example.SurveyBean.dto.response.SurveyResponse;
import com.example.SurveyBean.dto.response.SurveyResultResponse;
import com.example.SurveyBean.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<Void> createSurvey(@RequestBody SurveyCreateRequest request, Principal principal) {
        String username = principal.getName(); 
        surveyService.createSurvey(request, username);
        return ResponseEntity.status(201).build(); // 201 Created가 더 적절.
    }

    @GetMapping
    public ResponseEntity<List<SurveyResponse>> getSurveys() {
        List<SurveyResponse> surveys = surveyService.getSurveys();
        return ResponseEntity.ok(surveys);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponse> getSurveyById(@PathVariable Long id) {
        SurveyResponse survey = surveyService.getSurveyById(id);
        return ResponseEntity.ok(survey);
    }

    @PostMapping("/{id}/responses")
    public ResponseEntity<Void> submitAnswers(@PathVariable Long id, @RequestBody AnswerSubmitRequest request, Principal principal) {
        String username = principal.getName(); 
        surveyService.submitAnswers(id, request, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<SurveyResultResponse> getSurveyResults(@PathVariable Long id) {
        SurveyResultResponse results = surveyService.getSurveyResults(id);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        surveyService.deleteSurvey(id, username);
        return ResponseEntity.noContent().build();
    }
}
