package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.DevelopmentDirectionDto;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.employee.DevelopmentDirectionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeDevelopmentDirectionService {
    private final DevelopmentDirectionRepository developmentDirectionRepository;
    private final EmployeeMapper employeeMapper;

    public List<DevelopmentDirectionDto> findAll() {
        return developmentDirectionRepository.findAll()
            .stream().map(e -> employeeMapper.toDto(e)).toList();
    }

    public DevelopmentDirectionDto findById(Long id) {
        return employeeMapper.toDto(
            developmentDirectionRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Development direction not found: " + id))
        );
    }

    @Transactional
    public DevelopmentDirectionDto save(DevelopmentDirectionDto developmentDirectionDto) {
        return employeeMapper.toDto(
            developmentDirectionRepository.save(
                employeeMapper.toEntity(developmentDirectionDto)
            )
        );
    }

    @Transactional
    public void deleteById(Long id) {
        developmentDirectionRepository.deleteById(id);
    }
}
