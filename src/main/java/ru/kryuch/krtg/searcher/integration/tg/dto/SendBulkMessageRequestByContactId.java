package ru.kryuch.krtg.searcher.integration.tg.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendBulkMessageRequestByContactId {

    private ChatIdsRequest contacts;
    private String messageText;
    private Integer delaySeconds;
    private Boolean clearPrevious;

}
