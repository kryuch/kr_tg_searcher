package ru.kryuch.krtg.searcher.integration.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    List<ChatKey> excludeChats;
    SearchLastMessageType lastMessageType;
    PersonalChatType botType;
    PersonalChatType groupType;
    Boolean excludeStatusFlag;

    @ToString.Exclude
    List<ChatKey> excludeChats;

   Integer messagesCount;
    List <Integer> tgAccounts;
}
