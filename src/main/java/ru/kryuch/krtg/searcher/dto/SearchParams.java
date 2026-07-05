package ru.kryuch.krtg.searcher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kryuch.krtg.searcher.type.PersonalChatType;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchParams {

    String term = "Java";
    Integer maxFoundCount = 10;
    Integer minDiffDaysCount = 7;
    String lastMessage = "";
    PersonalChatType botType = PersonalChatType.PERSONAL;
    PersonalChatType groupType = PersonalChatType.PERSONAL;
    Boolean excludeStatusFlag = true;
    List<Long> excludeChatIds = new ArrayList<>();
    Integer messagesCount = 0;
}
