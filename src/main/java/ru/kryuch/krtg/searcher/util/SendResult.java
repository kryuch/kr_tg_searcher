package ru.kryuch.krtg.searcher.util;

import lombok.Data;

@Data
public class SendResult {
    private String id;
    private String name;
    private String username;
    private String status;
    private String error;

    public Long getNumericId() {
        if (id.matches("-?\\d+(\\.\\d+)?")) return Long.valueOf(id);

        return 0L;
    }
}
