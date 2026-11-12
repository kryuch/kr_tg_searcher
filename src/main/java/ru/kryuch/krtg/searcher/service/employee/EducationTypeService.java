package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.entity.employee.EducationTypeEntity;
import ru.kryuch.krtg.searcher.repository.employee.EducationTypeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationTypeService {

    private final EducationTypeRepository educationTypeRepository;

    public List<EducationTypeEntity> findAll() {
        return educationTypeRepository.findAll();
    }

    public EducationTypeEntity findById(Long id) {
        return educationTypeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Education type not found: " + id
            ));
    }

    @Transactional
    public EducationTypeEntity save(EducationTypeEntity entity) {
        return educationTypeRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        educationTypeRepository.deleteById(id);
    }
}
