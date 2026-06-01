package ru.kryuch.krtg.searcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Message {

    LocalDateTime dateTime;
    String value;
    Boolean ownerFlag;
}