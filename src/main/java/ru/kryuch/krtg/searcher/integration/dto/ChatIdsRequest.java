package ru.kryuch.krtg.searcher.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatIdsRequest {

    @ToString.Exclude
    private List<ChatIdsRequestItem> chatIds;
}
