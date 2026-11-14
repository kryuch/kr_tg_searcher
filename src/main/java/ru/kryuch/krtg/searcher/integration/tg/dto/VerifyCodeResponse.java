package ru.kryuch.krtg.searcher.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyCodeResponse {

    private boolean success;
    private String status;

}
