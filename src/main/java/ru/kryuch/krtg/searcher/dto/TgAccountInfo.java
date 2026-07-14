package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

@Data
public class TgAccountInfo {

    private Integer id;
    private String appId;
    private String appHash;
    private String description;
    private String phone;
    private Boolean isAuth;
}