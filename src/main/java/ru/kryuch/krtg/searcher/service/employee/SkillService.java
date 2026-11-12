package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.entity.employee.SkillEntity;
import ru.kryuch.krtg.searcher.repository.employee.SkillRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;

    public List<SkillEntity> findAll() {
        return skillRepository.findAll();
    }

    public SkillEntity findById(Long id) {
        return skillRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Skill not found: " + id
            ));
    }

    @Transactional
    public SkillEntity save(SkillEntity entity) {
        return skillRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        skillRepository.deleteById(id);
    }
}
