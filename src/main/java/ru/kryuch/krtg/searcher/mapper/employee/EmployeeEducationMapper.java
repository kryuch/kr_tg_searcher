package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import org.springframework.beans.factory.annotation.Autowired;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeEducationDto;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeEducationEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;
import ru.kryuch.krtg.searcher.repository.education.EducationDegreeRepository;
import ru.kryuch.krtg.searcher.repository.education.EducationSpecialtyRepository;
import ru.kryuch.krtg.searcher.repository.education.EducationTypeRepository;
import ru.kryuch.krtg.searcher.repository.education.EducationUniversityRepository;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EmployeeEducationMapper implements TMapper<EmployeeEducationEntity, EmployeeEducationDto> {

    @Autowired
    protected EducationUniversityRepository universityRepository;
    @Autowired
    protected EducationSpecialtyRepository specialtyRepository;
    @Autowired
    protected EducationDegreeRepository degreeRepository;
    @Autowired
    protected EducationTypeRepository educationTypeRepository;

    @Mapping(target = "universityId", source = "university.id")
    @Mapping(target = "educationTypeId", source = "educationType.id")
    @Mapping(target = "specialityId", source = "speciality.id")
    @Mapping(target = "degreeId", source = "degree.id")
    public abstract EmployeeEducationDto fromEntity(EmployeeEducationEntity entity);

    @AfterMapping
    protected void afterToEntity(@MappingTarget EmployeeEducationEntity entity, EmployeeEducationDto dto) {
        entity.setUniversity(universityRepository.findById(dto.getUniversityId()).orElseThrow());
        entity.setSpeciality(specialtyRepository.findById(dto.getSpecialityId()).orElseThrow());
        entity.setDegree(degreeRepository.findById(dto.getDegreeId()).orElseThrow());
        entity.setEducationType(educationTypeRepository.findById(dto.getEducationTypeId()).orElseThrow());
    }


}
