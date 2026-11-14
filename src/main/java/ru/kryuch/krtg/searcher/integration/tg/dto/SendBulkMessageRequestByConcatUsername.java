package ru.kryuch.krtg.searcher.integration.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class SendBulkMessageRequestByConcatUsername {

    private Set<String> contactUsernames;
    private String messageText;
    private Integer delaySeconds;
    private Boolean onlyNewChats;
    private Integer tgAccountId;
}
