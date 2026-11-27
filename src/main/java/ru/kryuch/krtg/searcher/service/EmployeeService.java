package ru.kryuch.krtg.searcher.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ru.kryuch.krtg.searcher.dto.EmployeeDto;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentGradeDto;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentSkillDto;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentSpecializationDto;
import ru.kryuch.krtg.searcher.dto.education.EducationDegreeDto;
import ru.kryuch.krtg.searcher.dto.education.EducationSpecialtyDto;
import ru.kryuch.krtg.searcher.dto.education.EducationTypeDto;
import ru.kryuch.krtg.searcher.dto.education.EducationUniversityDto;
import ru.kryuch.krtg.searcher.dto.location.LocationCityDto;
import ru.kryuch.krtg.searcher.dto.location.LocationLanguageDto;
import ru.kryuch.krtg.searcher.dto.location.LocationStateDto;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.helper.EmployeeHelper;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.employee.EmployeeRepository;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeFilterForVacancyDto;
import ru.kryuch.krtg.searcher.service.employee.EmployeeDevelopmentService;
import ru.kryuch.krtg.searcher.service.employee.EmployeeEducationService;
import ru.kryuch.krtg.searcher.service.employee.EmployeeLocationService;

@Service
@Transactional(readOnly = true)
@Slf4j
public class EmployeeService extends AbstractService<Long, EmployeeEntity, EmployeeDto, EmployeeMapper, EmployeeRepository> {

    private final EmployeeHelper employeeHelper;
    private final EmployeeEducationService employeeEducationService;
    private final EmployeeDevelopmentService employeeDevelopmentService;
    private final EmployeeLocationService employeeLocationService;

    public EmployeeService(final EmployeeRepository repository, final EmployeeMapper mapper, final EmployeeHelper employeeHelper, final EmployeeEducationService employeeEducationService, final EmployeeDevelopmentService employeeDevelopmentService, final EmployeeLocationService employeeLocationService) {
        super(repository, mapper, "Employee");
        this.employeeHelper = employeeHelper;
        this.employeeEducationService = employeeEducationService;
        this.employeeDevelopmentService = employeeDevelopmentService;
        this.employeeLocationService = employeeLocationService;
    }

    public List<Long> getResumeIdsByFilter(EmployeeFilterForVacancyDto filter) {
        log.info("EmployeeService::getResumeIdsByFilter (filter={}", filter);
        return null;
    }

    @Transactional
    public EmployeeDto save(EmployeeDto dto) {
        return mapper.fromEntity(repository.save(employeeHelper.dtoToEntity(dto)));
    }

    public List<EducationTypeDto> getEducationTypes() {
        return employeeEducationService.getTypes();
    }

    public List<EducationDegreeDto> getEducationDegree() {
        return employeeEducationService.getDegrees();
    }

    public List<EducationUniversityDto> getUniversities() {
        return employeeEducationService.getUniversities();
    }

    public List<EducationSpecialtyDto> getSpecialities() {
        return employeeEducationService.getSpecialities();
    }

    public List<DevelopmentSpecializationDto> getDevelopmentSpecializations() {
        return employeeDevelopmentService.getSpecializations();
    }

    public List<DevelopmentSkillDto> getDevelopmentSkills() {
        return employeeDevelopmentService.getSkills();
    }

    public List<DevelopmentGradeDto> getDevelopmentGrades() {
        return employeeDevelopmentService.getGrades();
    }

    public List<LocationLanguageDto> getLanguages() {
        return employeeLocationService.getLangs();
    }

    public List<LocationStateDto> getStates() {
        return employeeLocationService.getStates();
    }
}
