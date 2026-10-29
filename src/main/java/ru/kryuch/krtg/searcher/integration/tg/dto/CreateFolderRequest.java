package ru.kryuch.krtg.searcher.integration.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateFolderRequest {

    private Integer accountId;
    private String title;
}