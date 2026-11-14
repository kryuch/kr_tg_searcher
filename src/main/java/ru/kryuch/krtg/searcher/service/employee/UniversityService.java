package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.UniversityDto;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.employee.UniversityRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UniversityService {
    private final UniversityRepository universityRepository;
    private final EmployeeMapper employeeMapper;

    public List<UniversityDto> findAll() {
        return universityRepository.findAll()
            .stream().map(e -> employeeMapper.toDto(e)).toList();
    }

    public UniversityDto findById(Long id) {
        return employeeMapper.toDto(universityRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("University not found: " + id))
        );
    }

    @Transactional
    public UniversityDto save(UniversityDto dto) {
        return employeeMapper.toDto(universityRepository.save(
            employeeMapper.toEntity(dto)
        ));
    }

    @Transactional
    public void deleteById(Long id) {
        universityRepository.deleteById(id);
    }
}
