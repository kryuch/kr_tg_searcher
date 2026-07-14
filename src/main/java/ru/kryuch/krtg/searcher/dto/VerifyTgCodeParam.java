package ru.kryuch.krtg.searcher.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyTgCodeParam {

    private Integer tgAccountId;
    private String code;
    private String password;
}
