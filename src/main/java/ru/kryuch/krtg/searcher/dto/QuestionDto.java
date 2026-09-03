package ru.kryuch.krtg.searcher.dto;

import lombok.Builder;
import lombok.Data;


import java.util.Set;

@Data
@Builder
public class QuestionDto {

    private Integer id;
    private String text;
    private Integer questionType;
}
