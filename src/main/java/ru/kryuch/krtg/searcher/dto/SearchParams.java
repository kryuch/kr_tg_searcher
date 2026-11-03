package ru.kryuch.krtg.searcher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.kryuch.krtg.searcher.type.PersonalChatType;
import ru.kryuch.krtg.searcher.type.SearchLastMessageType;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchParams {
    @Builder.Default
    String term = "Java";

    @Builder.Default
    Integer maxFoundCount = 10;

    @Builder.Default
    Integer minDiffDaysCount = 7;

    @Builder.Default
    String lastMessage = "";

    SearchLastMessageType lastMessageType;

    @Builder.Default
    PersonalChatType botType = PersonalChatType.PERSONAL;

    @Builder.Default
    PersonalChatType groupType = PersonalChatType.PERSONAL;

    @Builder.Default
    Boolean excludeStatusFlag = true;

    @Builder.Default
    @ToString.Exclude
    List<ChatKey> excludeChats = new ArrayList<>();

    @Builder.Default
    Integer messagesCount = 0;

    @Builder.Default
    List <Integer> tgAccountIds = new ArrayList<>();
}
