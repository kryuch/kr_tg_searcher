package ru.kryuch.krtg.searcher.mapper.employee;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ru.kryuch.krtg.searcher.dto.employee.EmployeeEducationDto;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeEducationEntity;
import ru.kryuch.krtg.searcher.entity.employee.UniversityEntity;
import ru.kryuch.krtg.searcher.entity.employee.EducationTypeEntity;
import ru.kryuch.krtg.searcher.entity.employee.SpecialtyEntity;

class EmployeeEducationMapperTest {

    private final EmployeeEducationMapper mapper =
            Mappers.getMapper(EmployeeEducationMapper.class);

    private EmployeeEducationEntity entity;

    @BeforeEach
    void setUp() {
        UniversityEntity university = new UniversityEntity();
        university.setId(1L);
        university.setName("Harvard University");

        EducationTypeEntity educationType = new EducationTypeEntity();
        educationType.setId(2L);
        educationType.setName("Bachelor");

        SpecialtyEntity specialty = new SpecialtyEntity();
        specialty.setId(3L);
        specialty.setName("Computer Science");

        entity = new EmployeeEducationEntity();
        entity.setId(10L);
        entity.setUniversity(university);
        entity.setEducationType(educationType);
        entity.setSpecialty(specialty);
        entity.setStartYear(2015);
        entity.setEndYear(2019);
    }

    @Test
    void toDto_shouldMapAllFields() {
        EmployeeEducationDto dto = mapper.toDto(entity);

        assertNotNull(dto);

        assertEquals(10L, dto.getId());
        assertEquals("Harvard University", dto.getUniversity());
        assertEquals("Bachelor", dto.getEducationType());
        assertEquals("Computer Science", dto.getSpecialty());
        assertEquals(2015, dto.getStartYear());
        assertEquals(2019, dto.getEndYear());
    }

    @Test
    void toEntity_shouldMapSimpleFields() {
        EmployeeEducationDto dto = new EmployeeEducationDto();

        dto.setId(10L);
        dto.setUniversity("Harvard University");
        dto.setEducationType("Bachelor");
        dto.setSpecialty("Computer Science");
        dto.setStartYear(2015);
        dto.setEndYear(2019);

        EmployeeEducationEntity result = mapper.toEntity(dto);

        assertNotNull(result);

        assertEquals(10L, result.getId());
        assertEquals(2015, result.getStartYear());
        assertEquals(2019, result.getEndYear());

        assertNull(result.getUniversity());
        assertNull(result.getEducationType());
        assertNull(result.getSpecialty());
    }

    @Test
    void updateEntity_shouldUpdateSimpleFieldsAndPreserveRelationships() {
        EmployeeEducationDto dto = new EmployeeEducationDto();

        dto.setStartYear(2020);
        dto.setEndYear(2024);

        mapper.updateEntity(dto, entity);

        assertEquals(10L, entity.getId());

        assertEquals(2020, entity.getStartYear());
        assertEquals(2024, entity.getEndYear());

        assertEquals("Harvard University", entity.getUniversity().getName());
        assertEquals("Bachelor", entity.getEducationType().getName());
        assertEquals("Computer Science", entity.getSpecialty().getName());
    }
}
