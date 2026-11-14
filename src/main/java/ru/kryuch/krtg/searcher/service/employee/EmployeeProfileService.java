package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.employee.ProfileDto;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.employee.EmployeeProfileRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeProfileService {

    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeMapper employeeMapper;

    public List<ProfileDto> findAll() {
        return employeeProfileRepository.findAllByOrderByIdAscWithDetails()
            .stream().map(e -> employeeMapper.toDto(e)).toList();
    }

    public ProfileDto findById(Long id) {
        return employeeMapper.toDto(employeeProfileRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Profile not found: " + id
            )));
    }

    @Transactional
    public ProfileDto save(ProfileDto dto) {
        return employeeMapper.toDto(employeeProfileRepository.save(
            employeeMapper.toEntity(dto)    
        ));
    }

    @Transactional
    public void deleteById(Long id) {
        employeeProfileRepository.deleteById(id);
    }
}
