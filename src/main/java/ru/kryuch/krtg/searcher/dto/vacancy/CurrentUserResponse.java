package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUserResponse {

    private Long id;
    private String username;
    private String email;

 /*   public static CurrentUserResponse from(User user) {
        return CurrentUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }*/
}