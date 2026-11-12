package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.kryuch.krtg.searcher.type.vacancy.WorkFormat;

import java.time.LocalDate;

@Getter
@Setter
public class VacancyFilter {

    private String status;

    private WorkFormat workFormat;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdTo;
}