package ru.kryuch.krtg.searcher.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;
import ru.kryuch.krtg.searcher.type.PersonalChatType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    String term;
    Integer maxFoundCount;
    Integer minDiffDaysCount;
    String lastMessage;
    PersonalChatType botType;
    PersonalChatType groupType;
    Boolean excludeStatusFlag;
    List<Long> excludeChatIds;
    Integer messagesCount;
    List <Integer> tgAccounts;
}
