package ru.kryuch.krtg.searcher.service.employee;

import static org.junit.jupiter.api.DynamicTest.stream;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.EducationTypeDto;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.employee.EducationTypeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationTypeService {
    private final EducationTypeRepository educationTypeRepository;
    private final EmployeeMapper employeeMapper;

    public List<EducationTypeDto> findAll() {
        return educationTypeRepository.findAll()
            .stream().map(e -> employeeMapper.toDto(e)).toList();
    }

    public EducationTypeDto findById(Long id) {
        return employeeMapper.toDto(educationTypeRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Education type not found: " + id))
        );
    }

    @Transactional
    public EducationTypeDto save(EducationTypeDto dto) {
        return employeeMapper.toDto(educationTypeRepository.save(
            employeeMapper.toEntity(dto)
        ));
    }

    @Transactional
    public void deleteById(Long id) {
        educationTypeRepository.deleteById(id);
    }
}
