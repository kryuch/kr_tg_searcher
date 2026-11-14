package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.SpecialtyDto;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.employee.SpecialtyRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    private final EmployeeMapper employeeMapper;

    public List<SpecialtyDto> findAll() {
        return specialtyRepository.findAll()
            .stream().map(e -> employeeMapper.toDto(e)).toList();
    }

    public SpecialtyDto findById(Long id) {
        return employeeMapper.toDto(specialtyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Specialty not found: " + id))
        );
    }

    @Transactional
    public SpecialtyDto save(SpecialtyDto dto) {
        return employeeMapper.toDto(specialtyRepository.save(
            employeeMapper.toEntity(dto)
        ));
    }

    @Transactional
    public void deleteById(Long id) {
        specialtyRepository.deleteById(id);
    }
}
