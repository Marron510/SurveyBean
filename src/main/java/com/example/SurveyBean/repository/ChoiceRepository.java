package com.example.SurveyBean.repository;

import com.example.SurveyBean.domain.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
