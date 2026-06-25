package ru.kryuch.krtg.searcher.integration.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class SendBulkMessageRequest {

    private Set<Long> chatIds;
    private String messageText;
    private Integer delaySeconds;
    private Boolean onlyNewChats;
}
