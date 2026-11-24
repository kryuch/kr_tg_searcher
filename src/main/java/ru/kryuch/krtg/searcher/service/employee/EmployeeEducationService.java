package ru.kryuch.krtg.searcher.service.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.education.EducationDegreeDto;
import ru.kryuch.krtg.searcher.dto.education.EducationSpecialtyDto;
import ru.kryuch.krtg.searcher.dto.education.EducationTypeDto;
import ru.kryuch.krtg.searcher.dto.education.EducationUniversityDto;
import ru.kryuch.krtg.searcher.service.education.EducationDegreeService;
import ru.kryuch.krtg.searcher.service.education.EducationSpecialtyService;
import ru.kryuch.krtg.searcher.service.education.EducationTypeService;
import ru.kryuch.krtg.searcher.service.education.EducationUniversityService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeEducationService {

    private final EducationUniversityService educationUniversityService;
    private final EducationSpecialtyService educationSpecialtyService;
    private final EducationTypeService educationTypeService;
    private final EducationDegreeService educationDegreeService;

    public List<EducationTypeDto> getTypes() {
        return educationTypeService.getAll();
    }

    public List<EducationUniversityDto> getUniversities() {
        return educationUniversityService.getAll();
    }

    public List<EducationSpecialtyDto> getSpecialities() {
        return educationSpecialtyService.getAll();
    }

    public List<EducationDegreeDto> getDegrees() {
        return educationDegreeService.getAll();
    }
}
