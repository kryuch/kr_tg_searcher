package ru.kryuch.krtg.searcher.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFileDto {

    private Integer id;
    private String originalName;
    private String storageName;
    private String contentType;
    private Long size;
}
