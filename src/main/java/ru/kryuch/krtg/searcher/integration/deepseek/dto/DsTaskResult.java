package ru.kryuch.krtg.searcher.integration.deepseek.dto;

import lombok.Data;

@Data
public class DsTaskResult {

    private Long id;
    private Integer groupId;
    private String value;
}