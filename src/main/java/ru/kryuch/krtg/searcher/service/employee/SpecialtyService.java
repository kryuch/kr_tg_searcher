package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.entity.employee.SpecialtyEntity;
import ru.kryuch.krtg.searcher.repository.employee.SpecialtyRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public List<SpecialtyEntity> findAll() {
        return specialtyRepository.findAll();
    }

    public SpecialtyEntity findById(Long id) {
        return specialtyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Specialty not found: " + id
            ));
    }

    @Transactional
    public SpecialtyEntity save(SpecialtyEntity entity) {
        return specialtyRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        specialtyRepository.deleteById(id);
    }
}
