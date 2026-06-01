package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MessagesHistory {

    private List<Message> values = new ArrayList<>();

    public void add(Message message) {
        values.add(message);
    }

    public int size() {
        return values.size();
    }
}
