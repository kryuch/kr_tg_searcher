package ru.kryuch.krtg.searcher.integration.mail.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MailSendRequest {

    private String title;
    private String to;
    private String from;
    private String body;
    private String googleRefreshToken;
    private List<MailAttachment> attachments;
}
