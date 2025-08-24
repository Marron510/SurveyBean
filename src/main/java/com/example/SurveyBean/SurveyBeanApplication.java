package com.example.SurveyBean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // Added import

@EnableJpaAuditing // Added annotation
@SpringBootApplication
public class SurveyBeanApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurveyBeanApplication.class, args);
	}

}
