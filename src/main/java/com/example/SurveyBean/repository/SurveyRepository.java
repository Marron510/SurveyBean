package com.example.SurveyBean.repository;

import com.example.SurveyBean.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    @Query("SELECT s FROM Survey s JOIN FETCH s.questions WHERE s.id = :id")
    Optional<Survey> findByIdWithQuestions(Long id);
}
