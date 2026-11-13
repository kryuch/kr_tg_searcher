package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.entity.employee.ProfileEntity;
import ru.kryuch.krtg.searcher.repository.employee.EmployeeProfileRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeProfileService {

    private final EmployeeProfileRepository employeeProfileRepository;

    public List<ProfileEntity> findAll() {
        return employeeProfileRepository.findAllByOrderByIdAscWithDetails();
    }

    public ProfileEntity findById(Long id) {
        return employeeProfileRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Profile not found: " + id
            ));
    }

    @Transactional
    public ProfileEntity save(ProfileEntity entity) {
        return employeeProfileRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        employeeProfileRepository.deleteById(id);
    }
}
