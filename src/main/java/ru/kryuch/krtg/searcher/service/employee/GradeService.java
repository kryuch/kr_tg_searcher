package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.entity.employee.GradeEntity;
import ru.kryuch.krtg.searcher.repository.employee.GradeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeService {

    private final GradeRepository gradeRepository;

    public List<GradeEntity> findAll() {
        return gradeRepository.findAll();
    }

    public GradeEntity findById(Long id) {
        return gradeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Grade not found: " + id
            ));
    }

    @Transactional
    public GradeEntity save(GradeEntity entity) {
        return gradeRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        gradeRepository.deleteById(id);
    }
}
