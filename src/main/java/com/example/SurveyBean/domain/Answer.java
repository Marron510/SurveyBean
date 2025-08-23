package com.example.SurveyBean.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // 객관식 질문용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "choice_id") // 널 허용
    private Choice choice;

    // 주관식 질문용
    @Column
    private String content;

    @Builder
    public Answer(User user, Question question, Choice choice, String content) {
        this.user = user;
        this.question = question;
        this.choice = choice;
        this.content = content;
    }
}
