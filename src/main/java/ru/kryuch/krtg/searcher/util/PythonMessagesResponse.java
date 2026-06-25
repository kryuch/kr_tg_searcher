package ru.kryuch.krtg.searcher.util;

import lombok.Data;
import ru.kryuch.krtg.searcher.dto.Message;

import java.util.List;

@Data
public class PythonMessagesResponse {
    private List<Message> messages;
}