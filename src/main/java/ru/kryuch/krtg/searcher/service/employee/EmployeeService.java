package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeDto;
import ru.kryuch.krtg.searcher.mapper.employee.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.employee.EmployeeRepository;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeFilterForVacancyDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public List<Long> getResumeIdsByFilter(EmployeeFilterForVacancyDto filter) {
        log.info("EmployeeService::getResumeIdsByFilter (filter={}", filter);
        return null;
    }

    public List<EmployeeDto> findAll() {
        return employeeRepository.findAllByOrderByIdAsc()
            .stream().map(e -> employeeMapper.toDto(e)).toList();
    }

    public EmployeeDto findById(Long id) {
        return employeeMapper.toDto(
            employeeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id))
        );
    }

    @Transactional
    public EmployeeDto save(EmployeeDto dto) {
        return employeeMapper.toDto(employeeRepository.save(
            employeeMapper.toEntity(dto)
        ));
    }

    @Transactional
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }
}
