package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExtensionTokenResponse {

    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}