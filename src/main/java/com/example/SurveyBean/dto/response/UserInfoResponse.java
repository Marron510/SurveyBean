package com.example.SurveyBean.dto.response;

import com.example.SurveyBean.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
    private String username;
    private String email;

    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
