package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatInfo {
    String name;
    String username;
    String avatar;
    Long id;
    Boolean actual = true;
    Integer status = 0;


    Integer sendStatus;
    String comment;

    List<Message> messages;
}
