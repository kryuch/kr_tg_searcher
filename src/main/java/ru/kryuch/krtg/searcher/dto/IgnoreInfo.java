package ru.kryuch.krtg.searcher.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class IgnoreInfo {

    private Long id;

    private String username;
}
