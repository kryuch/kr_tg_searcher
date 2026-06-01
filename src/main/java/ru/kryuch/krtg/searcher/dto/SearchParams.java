package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

@Data
public class SearchParams {

    String term = "Java";
    Integer maxFoundCount = 10;
    Integer minDiffDaysCount = 7;
    Boolean excludeBotFlag = true;
    Boolean excludeGroupFlag = true;
    Boolean excludeStatusFlag = true;
}
