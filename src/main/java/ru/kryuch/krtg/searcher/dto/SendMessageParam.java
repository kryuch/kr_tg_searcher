package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

import java.util.List;

@Data
public class SendMessageParam {

    private Integer tgAccountId;
    private List<Long> chatIds;
    private String message;
    private Long back;

}
