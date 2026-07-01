package ru.kryuch.krtg.searcher.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatIdsRequest {

    private List<Long> chatIds;
}
