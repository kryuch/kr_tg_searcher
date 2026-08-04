package ru.kryuch.krtg.searcher.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;

import java.util.List;

@Data
@AllArgsConstructor
public class InitRequest {

    private List<TgAccountInfo> items;
}
