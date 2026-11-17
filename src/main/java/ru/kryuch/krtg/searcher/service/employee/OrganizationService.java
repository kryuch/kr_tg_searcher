package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.OrganizationDto;
import ru.kryuch.krtg.searcher.mapper.employee.OrganizationMapper;
import ru.kryuch.krtg.searcher.repository.employee.OrganizationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    public List<OrganizationDto> findAll() {
        return organizationRepository.findAll()
            .stream().map(e -> organizationMapper.toDto(e)).toList();
    }

    public OrganizationDto findById(Long id) {
        return organizationMapper.toDto(organizationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Organization not found: " + id))
        );
    }

    @Transactional
    public OrganizationDto save(OrganizationDto dto) {
        return organizationMapper.toDto(organizationRepository.save(
            organizationMapper.toEntity(dto)
        ));
    }

    @Transactional
    public void deleteById(Long id) {
        organizationRepository.deleteById(id);
    }
}
