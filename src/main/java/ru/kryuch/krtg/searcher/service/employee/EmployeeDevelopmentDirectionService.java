package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.DevelopmentDirectionDto;
import ru.kryuch.krtg.searcher.mapper.employee.DevelopmentDirectionMapper;
import ru.kryuch.krtg.searcher.repository.employee.DevelopmentDirectionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeDevelopmentDirectionService {
    private final DevelopmentDirectionRepository developmentDirectionRepository;
    private final DevelopmentDirectionMapper developmentDirectionMapper;

    public List<DevelopmentDirectionDto> findAll() {
        return developmentDirectionRepository.findAll()
            .stream().map(e -> developmentDirectionMapper.toDto(e)).toList();
    }

    public DevelopmentDirectionDto findById(Long id) {
        return developmentDirectionMapper.toDto(
            developmentDirectionRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Development direction not found: " + id))
        );
    }

    @Transactional
    public DevelopmentDirectionDto save(DevelopmentDirectionDto developmentDirectionDto) {
        return developmentDirectionMapper.toDto(
            developmentDirectionRepository.save(
                developmentDirectionMapper.toEntity(developmentDirectionDto)
            )
        );
    }

    @Transactional
    public void deleteById(Long id) {
        developmentDirectionRepository.deleteById(id);
    }
}
