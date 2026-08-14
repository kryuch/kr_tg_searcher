package ru.kryuch.krtg.searcher.integration.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.kryuch.krtg.searcher.dto.ChatKey;
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;
import ru.kryuch.krtg.searcher.type.PersonalChatType;
import ru.kryuch.krtg.searcher.type.SearchLastMessageType;

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
    SearchLastMessageType lastMessageType;
    PersonalChatType botType;
    PersonalChatType groupType;
    Boolean excludeStatusFlag;

    @ToString.Exclude
    List<ChatKey> excludeChats;

    Integer messagesCount;
    List <Integer> tgAccounts;
}
