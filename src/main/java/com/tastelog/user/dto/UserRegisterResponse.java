package com.tastelog.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegisterResponse {
    private Long id;
    private String email;
    private String nickname;
}