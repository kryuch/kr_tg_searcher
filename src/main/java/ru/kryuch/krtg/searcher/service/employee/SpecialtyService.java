package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.SpecialtyDto;
import ru.kryuch.krtg.searcher.mapper.employee.SpecialtyMapper;
import ru.kryuch.krtg.searcher.repository.employee.SpecialtyRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    private final SpecialtyMapper specialtyMapper;

    public List<SpecialtyDto> findAll() {
        return specialtyRepository.findAll()
            .stream().map(e -> specialtyMapper.toDto(e)).toList();
    }

    public SpecialtyDto findById(Long id) {
        return specialtyMapper.toDto(specialtyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Specialty not found: " + id))
        );
    }

    @Transactional
    public SpecialtyDto save(SpecialtyDto dto) {
        return specialtyMapper.toDto(specialtyRepository.save(
            specialtyMapper.toEntity(dto)
        ));
    }

    @Transactional
    public void deleteById(Long id) {
        specialtyRepository.deleteById(id);
    }
}
