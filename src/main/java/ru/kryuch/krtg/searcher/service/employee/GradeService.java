package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.GradeDto;
import ru.kryuch.krtg.searcher.entity.employee.GradeEntity;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.employee.GradeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeService {

    private final GradeRepository gradeRepository;

    private final EmployeeMapper employeeMapper;

    public List<GradeDto> findAll() {
        return gradeRepository.findAll()
            .stream().map(e -> employeeMapper.toDto(e)).toList();
    }

    public GradeDto findById(Long id) {
        return employeeMapper.toDto(
            gradeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Grade not found: " + id))
        );
    }

    @Transactional
    public GradeDto save(GradeDto entity) {
        return employeeMapper.toDto(gradeRepository.save(
            employeeMapper.toEntity(entity)
        ));
    }

    @Transactional
    public void deleteById(Long id) {
        gradeRepository.deleteById(id);
    }
}
