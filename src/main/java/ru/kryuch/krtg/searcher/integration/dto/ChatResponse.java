package ru.kryuch.krtg.searcher.integration.dto;

import lombok.Data;
import ru.kryuch.krtg.searcher.dto.FolderInfo;
import ru.kryuch.krtg.searcher.dto.Message;
import ru.kryuch.krtg.searcher.type.ChatStatus;
import ru.kryuch.krtg.searcher.type.SendMessageStatus;

import java.util.List;

@Data
public class ChatResponse {

    String name;
    String username;
    String avatar;
    Long id;
    Boolean actual = true;
    ChatStatus status = ChatStatus.SIMPLE;
    List<FolderInfo> folders;
    Boolean hasTargetFolder;


    SendMessageStatus sendStatus;
    String comment;

    List<Message> messages;

    Integer tgAccountId;
}
