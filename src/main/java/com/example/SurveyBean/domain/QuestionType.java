package com.example.SurveyBean.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum QuestionType {
    SINGLE_CHOICE, // 단일 선택
    MULTIPLE_CHOICE, // 다중 선택
    TEXT; // 주관식

    @JsonCreator
    public static QuestionType fromValue(String value) {
        for (QuestionType type : QuestionType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown QuestionType: " + value);
    }
}
