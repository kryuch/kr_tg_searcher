package ru.kryuch.krtg.searcher.integration.deepseek.dto;

import lombok.Value;

@Value
public class DeepseekSetting {

    private final Integer groupId;
    private final Integer secondsInterval;
}