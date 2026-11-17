package ru.kryuch.krtg.searcher.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeFilterForVacancyDto;

import java.util.List;

@Service
@Slf4j
public class EmployeeService {

    public List<Long> getResumeIdsByFilter(EmployeeFilterForVacancyDto filter) {
        log.info("EmployeeService::getResumeIdsByFilter (filter={}", filter);
        return null;
    }
}
