package ru.kryuch.krtg.searcher.util;

import lombok.Data;

@Data
public class SendResult {
    private Long id;
    private String name;
    private String username;
    private String status;
    private String error;
}
