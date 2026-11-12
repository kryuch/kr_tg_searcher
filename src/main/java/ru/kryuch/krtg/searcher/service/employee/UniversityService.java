package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.entity.employee.UniversityEntity;
import ru.kryuch.krtg.searcher.repository.employee.UniversityRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UniversityService {

    private final UniversityRepository universityRepository;

    public List<UniversityEntity> findAll() {
        return universityRepository.findAll();
    }

    public UniversityEntity findById(Long id) {
        return universityRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "University not found: " + id
            ));
    }

    @Transactional
    public UniversityEntity save(UniversityEntity entity) {
        return universityRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        universityRepository.deleteById(id);
    }
}
