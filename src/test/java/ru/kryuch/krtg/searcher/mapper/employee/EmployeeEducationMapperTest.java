package ru.kryuch.krtg.searcher.mapper.employee;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ru.kryuch.krtg.searcher.dto.employee.EmployeeEducationDto;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeEducationEntity;
import ru.kryuch.krtg.searcher.entity.education.EducationUniversityEntity;
import ru.kryuch.krtg.searcher.entity.education.EducationTypeEntity;
import ru.kryuch.krtg.searcher.entity.education.EducationSpecialtyEntity;

class EmployeeEducationMapperTest {

    private final EmployeeEducationMapper mapper =
            Mappers.getMapper(EmployeeEducationMapper.class);

    private EmployeeEducationEntity entity;

    @BeforeEach
    void setUp() {
        EducationUniversityEntity university = new EducationUniversityEntity();
        university.setId(1L);
        university.setName("Harvard University");

        EducationTypeEntity educationType = new EducationTypeEntity();
        educationType.setId(2L);
        educationType.setName("Bachelor");

        EducationSpecialtyEntity specialty = new EducationSpecialtyEntity();
        specialty.setId(3L);
        specialty.setName("Computer Science");

        entity = new EmployeeEducationEntity();
        entity.setId(10L);
        entity.setUniversity(university);
        entity.setEducationType(educationType);
        entity.setSpeciality(specialty);
        entity.setStartYear(2015);
        entity.setEndYear(2019);
    }

    @Test
    void toDto_shouldMapAllFields() {
        EmployeeEducationDto dto = mapper.fromEntity(entity);

        assertNotNull(dto);

        assertEquals(10L, dto.getId());
        assertEquals(1L, dto.getUniversityId());
        assertEquals(2L, dto.getEducationTypeId());
        assertEquals(3L, dto.getSpecialityId());
        assertEquals(2015, dto.getStartYear());
        assertEquals(2019, dto.getEndYear());
    }

    @Test
    @Disabled
    void toEntity_shouldMapSimpleFields() {
        EmployeeEducationDto dto = new EmployeeEducationDto();

        dto.setId(10L);
        dto.setUniversityId(11L);//"Harvard University");
        dto.setEducationTypeId(11L);//"Bachelor");
        dto.setSpecialityId(11L);//"Computer Science");
        dto.setStartYear(2015);
        dto.setEndYear(2019);

        EmployeeEducationEntity result = mapper.toEntity(dto);

        assertNotNull(result);

        assertEquals(10L, result.getId());
        assertEquals(2015, result.getStartYear());
        assertEquals(2019, result.getEndYear());

        assertNull(result.getUniversity());
        assertNull(result.getEducationType());
        assertNull(result.getSpeciality());
    }

}
