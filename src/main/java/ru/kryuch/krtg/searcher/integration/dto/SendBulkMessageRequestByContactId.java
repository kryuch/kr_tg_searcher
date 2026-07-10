package ru.kryuch.krtg.searcher.integration.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class SendBulkMessageRequestByContactId {

    private ChatIdsRequest contacts;
    private String messageText;
    private Integer delaySeconds;

}
