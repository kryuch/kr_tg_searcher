package ru.kryuch.krtg.searcher.util;

import lombok.Data;

import java.util.List;

@Data
public class SendResponse {
    private int total;
    private int success;
    private int error;
    private List<SendResult> results;
}