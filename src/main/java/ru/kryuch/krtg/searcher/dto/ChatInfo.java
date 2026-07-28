package ru.kryuch.krtg.searcher.dto;

import lombok.Data;
import org.springframework.util.CollectionUtils;
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

    String tgAccount;

    public String getFolderTitles() {
        if (CollectionUtils.isEmpty(folders)) return "";

        return folders.stream()
                .map(item -> item.getTitle())
                .collect(Collectors.joining("/"));
    }
}
