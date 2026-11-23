package ru.kryuch.krtg.searcher.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ru.kryuch.krtg.searcher.dto.EmployeeDto;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.helper.EmployeeHelper;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.employee.EmployeeRepository;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeFilterForVacancyDto;

@Service
@Transactional(readOnly = true)
@Slf4j
public class EmployeeService extends AbstractService <Long, EmployeeEntity, EmployeeDto, EmployeeMapper, EmployeeRepository>{

    private final EmployeeHelper employeeHelper;

    public EmployeeService(final EmployeeRepository repository, final EmployeeMapper mapper, final EmployeeHelper employeeHelper) {
        super(repository, mapper, "Employee");
        this.employeeHelper = employeeHelper;
    }

    public List<Long> getResumeIdsByFilter(EmployeeFilterForVacancyDto filter) {
        log.info("EmployeeService::getResumeIdsByFilter (filter={}", filter);
        return null;
    }

    @Transactional
    public EmployeeDto save(EmployeeDto dto) {
        return mapper.fromEntity(repository.save(employeeHelper.dtoToEntity(dto)));
    }

}
