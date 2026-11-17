package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.UniversityDto;
import ru.kryuch.krtg.searcher.mapper.employee.UniversityMapper;
import ru.kryuch.krtg.searcher.repository.employee.UniversityRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UniversityService {
    private final UniversityRepository universityRepository;
    private final UniversityMapper universityMapper;

    public List<UniversityDto> findAll() {
        return universityRepository.findAll()
            .stream().map(e -> universityMapper.toDto(e)).toList();
    }

    public UniversityDto findById(Long id) {
        return universityMapper.toDto(universityRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("University not found: " + id))
        );
    }

    @Transactional
    public UniversityDto save(UniversityDto dto) {
        return universityMapper.toDto(universityRepository.save(
            universityMapper.toEntity(dto)
        ));
    }

    @Transactional
    public void deleteById(Long id) {
        universityRepository.deleteById(id);
    }
}
