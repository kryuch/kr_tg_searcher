package ru.kryuch.krtg.searcher.integration.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestCodeResponse {

    private boolean success;
    private String error;
    private Integer waitSeconds;
    private boolean authorised;
}
