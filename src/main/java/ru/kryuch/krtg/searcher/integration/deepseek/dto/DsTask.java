package ru.kryuch.krtg.searcher.integration.deepseek.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DsTask {

    private Boolean isReady;
    private String value;
    private Long id;
    private Integer groupId;
}