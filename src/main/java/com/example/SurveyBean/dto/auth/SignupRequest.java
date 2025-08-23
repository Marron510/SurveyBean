package com.example.SurveyBean.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private String nickname;
}
