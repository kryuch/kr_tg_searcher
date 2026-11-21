package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeProfileDto;
import ru.kryuch.krtg.searcher.mapper.employee.EmployeeProfileMapper;
import ru.kryuch.krtg.searcher.repository.employee.EmployeeProfileRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeProfileService {

    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeProfileMapper profileMapper;
/*
    public List<EmployeeProfileDto> findAll() {
        return employeeProfileRepository.findAllByOrderByIdAscWithDetails()
            .stream().map(e -> profileMapper.toDto(e)).toList();
    }

    public EmployeeProfileDto findById(Long id) {
        return profileMapper.toDto(employeeProfileRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Profile not found: " + id
            )));
    }

    @Transactional
    public EmployeeProfileDto save(EmployeeProfileDto dto) {
        return profileMapper.toDto(employeeProfileRepository.save(
            profileMapper.toEntity(dto)    
        ));
    }
*/
    @Transactional
    public void deleteById(Long id) {
        employeeProfileRepository.deleteById(id);
    }
}
