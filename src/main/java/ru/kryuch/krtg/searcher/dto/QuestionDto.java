package ru.kryuch.krtg.searcher.dto;

import lombok.Builder;
import lombok.Data;
import ru.kryuch.krtg.searcher.integration.hh.mapper.QuestionOption;

import java.util.Set;

@Data
@Builder
public class QuestionDto {

    private Integer id;
    private String text;
    private Integer questionType;
    private Set<QuestionOption> options;
}
