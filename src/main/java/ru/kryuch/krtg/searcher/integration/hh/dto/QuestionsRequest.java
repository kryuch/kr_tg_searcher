package ru.kryuch.krtg.searcher.integration.hh.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionsRequest {

    private List<QuestionsRequestItem> items;
}
