package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeSkillDto;
import ru.kryuch.krtg.searcher.mapper.employee.SkillMapper;
import ru.kryuch.krtg.searcher.repository.employee.SkillRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public List<EmployeeSkillDto> findAll() {
        return skillRepository.findAll()
            .stream().map(e -> skillMapper.toDto(e)).toList();
    }

    public EmployeeSkillDto findById(Long id) {
        return skillMapper.toDto(skillRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + id))
        );
    }

    @Transactional
    public EmployeeSkillDto save(EmployeeSkillDto dto) {
        return skillMapper.toDto(skillRepository.save(
            skillMapper.toEntity(dto))
        );
    }

    @Transactional
    public void deleteById(Long id) {
        skillRepository.deleteById(id);
    }
}
