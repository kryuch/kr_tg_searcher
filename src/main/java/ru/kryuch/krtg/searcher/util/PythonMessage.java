package ru.kryuch.krtg.searcher.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PythonMessage {
    private String text;

    @JsonProperty("date_str")
    private String dateStr;

    @JsonProperty("is_me")
    private boolean isMe;
}