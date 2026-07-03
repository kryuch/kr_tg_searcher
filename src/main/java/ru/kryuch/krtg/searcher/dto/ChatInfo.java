package ru.kryuch.krtg.searcher.dto;

import lombok.Data;
import ru.kryuch.krtg.searcher.type.ChatStatus;
import ru.kryuch.krtg.searcher.type.SendMessageStatus;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ChatInfo {
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

    public String getFolderTitles() {
        return folders.stream()
                .map(item -> item.getTitle())
                .collect(Collectors.joining("/"));
    }
}
