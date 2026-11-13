package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.entity.employee.DevelopmentDirectionEntity;
import ru.kryuch.krtg.searcher.repository.employee.DevelopmentDirectionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeDevelopmentDirectionService {

    private final DevelopmentDirectionRepository developmentDirectionRepository;

    public List<DevelopmentDirectionEntity> findAll() {
        return developmentDirectionRepository.findAll();
    }

    public DevelopmentDirectionEntity findById(Long id) {
        return developmentDirectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Development direction not found: " + id
            ));
    }

    @Transactional
    public DevelopmentDirectionEntity save(DevelopmentDirectionEntity entity) {
        return developmentDirectionRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        developmentDirectionRepository.deleteById(id);
    }
}
