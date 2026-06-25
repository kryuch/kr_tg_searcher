package ru.kryuch.krtg.searcher.dto;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.PersonalChatType;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchParams {

    String term = "Java";
    Integer maxFoundCount = 10;
    Integer minDiffDaysCount = 7;
    PersonalChatType botType = PersonalChatType.PERSONAL;
    PersonalChatType groupType = PersonalChatType.PERSONAL;
    Boolean excludeStatusFlag = true;
    List<Long> excludeChatIds = new ArrayList<>();
    Integer messagesCount = 0;
}
