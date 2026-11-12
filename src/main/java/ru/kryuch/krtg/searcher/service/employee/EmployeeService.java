package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeEntity;
import ru.kryuch.krtg.searcher.repository.employee.EmployeeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public List<EmployeeEntity> findAll() {
        return employeeRepository.findAllByOrderByIdAsc();
    }

    public EmployeeEntity findById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Employee not found: " + id
            ));
    }

    @Transactional
    public EmployeeEntity save(EmployeeEntity entity) {
        return employeeRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }
}
