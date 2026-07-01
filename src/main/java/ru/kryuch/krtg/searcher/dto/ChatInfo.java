package ru.kryuch.krtg.searcher.dto;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.ChatStatus;
import ru.kryuch.krtg.searcher.type.SendMessageStatus;

import java.util.List;

@Data
public class ChatInfo {
    String name;
    String username;
    String avatar;
    Long id;
    Boolean actual = true;
    ChatStatus status = ChatStatus.SIMPLE;
    List<FolderInfo> folders;


    SendMessageStatus sendStatus;
    String comment;

    List<Message> messages;
}
