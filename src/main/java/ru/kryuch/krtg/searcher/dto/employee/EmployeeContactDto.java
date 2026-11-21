package ru.kryuch.krtg.searcher.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.type.ContactType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeContactDto {

    private String value;

    private ContactType type;

}
