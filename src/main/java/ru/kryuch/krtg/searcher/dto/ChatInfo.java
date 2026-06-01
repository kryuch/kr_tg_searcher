package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

@Data
public class ChatInfo {
    String name;
    String avatar;
    Long id;
    Boolean actual = true;
    Integer status = 0;
}
