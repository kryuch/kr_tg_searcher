package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.SkillDto;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.employee.SkillRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {
    private final SkillRepository skillRepository;
    private final EmployeeMapper employeeMapper;

    public List<SkillDto> findAll() {
        return skillRepository.findAll()
            .stream().map(e -> employeeMapper.toDto(e)).toList();
    }

    public SkillDto findById(Long id) {
        return employeeMapper.toDto(skillRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + id))
        );
    }

    @Transactional
    public SkillDto save(SkillDto dto) {
        return employeeMapper.toDto(skillRepository.save(
            employeeMapper.toEntity(dto))
        );
    }

    @Transactional
    public void deleteById(Long id) {
        skillRepository.deleteById(id);
    }
}
