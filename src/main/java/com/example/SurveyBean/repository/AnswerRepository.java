package com.example.SurveyBean.repository;

import com.example.SurveyBean.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionIdIn(List<Long> questionIds);
}
