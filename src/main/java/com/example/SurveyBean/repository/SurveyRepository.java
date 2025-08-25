package com.example.SurveyBean.repository;

import com.example.SurveyBean.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    @Query("SELECT s FROM Survey s JOIN FETCH s.questions WHERE s.id = :id")
    Optional<Survey> findByIdWithQuestions(Long id);

    @Query("SELECT s FROM Survey s LEFT JOIN s.questions q LEFT JOIN q.answers a GROUP BY s.id ORDER BY COUNT(a) DESC")
    List<Survey> findTop3ByOrderByAnswersDesc(Pageable pageable);

    List<Survey> findByUser_Username(String username);
}
