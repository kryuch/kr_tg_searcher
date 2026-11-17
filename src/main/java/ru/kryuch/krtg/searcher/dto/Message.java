package ru.kryuch.krtg.searcher.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.text.StringEscapeUtils;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Message {

//    @JsonFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime dateTime;
    String value;
    Boolean ownerFlag;
    public String toLogString() {
        return StringEscapeUtils.escapeJava(this.toString());
    }
}