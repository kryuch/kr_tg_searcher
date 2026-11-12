package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.entity.employee.OrganizationEntity;
import ru.kryuch.krtg.searcher.repository.employee.OrganizationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public List<OrganizationEntity> findAll() {
        return organizationRepository.findAll();
    }

    public OrganizationEntity findById(Long id) {
        return organizationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Organization not found: " + id
            ));
    }

    @Transactional
    public OrganizationEntity save(OrganizationEntity entity) {
        return organizationRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        organizationRepository.deleteById(id);
    }
}
