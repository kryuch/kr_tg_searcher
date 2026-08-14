package ru.kryuch.krtg.searcher.integration.mail.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailSendRequest {

    private String title;
    private String to;
    private String from;
    private String body;
    private String googleRefreshToken;
}
