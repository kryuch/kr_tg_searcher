package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentGradeDto;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentSkillDto;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentSpecializationDto;
import ru.kryuch.krtg.searcher.service.development.DevelopmentGradeService;
import ru.kryuch.krtg.searcher.service.development.DevelopmentSkillService;
import ru.kryuch.krtg.searcher.service.development.DevelopmentSpecializationService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeDevelopmentService {
    private final DevelopmentSkillService skillService;
    private final DevelopmentGradeService gradeService;
    private final DevelopmentSpecializationService specializationService;

    public List<DevelopmentSpecializationDto> getSpecializations() {
        return specializationService.getAll();
    }

    public List <DevelopmentGradeDto> getGrades() {
        return gradeService.getAll();
    }

    public List <DevelopmentSkillDto> getSkills() {
        return skillService.getAll();
    }
}
