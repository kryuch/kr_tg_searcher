package ru.kryuch.krtg.searcher.integration.mail.dto;

import lombok.Data;

@Data
public class MailAttachment {
    private String fileName;
    private String contentType;
    private byte[] content;
}
