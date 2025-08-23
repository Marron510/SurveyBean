package com.example.SurveyBean.repository;

import com.example.SurveyBean.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
