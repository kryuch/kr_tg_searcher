package ru.kryuch.krtg.searcher.integration.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class SendBulkMessageRequestByContactId {

    private Set<Long> contactIds;
    private String messageText;
    private Integer delaySeconds;

}
