package ru.kryuch.krtg.searcher.dto.view;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendVacancyMessageResponse {

    private String success;
    private String error;
}