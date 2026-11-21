package ru.kryuch.krtg.searcher.mapper.employee;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeProfileDto;
import ru.kryuch.krtg.searcher.entity.GradeEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileSkillEntity;
import ru.kryuch.krtg.searcher.entity.employee.SkillEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeProfileMapperTest {

    private final EmployeeProfileMapper mapper = Mappers.getMapper(EmployeeProfileMapper.class);

    @Test
    void fromEntity_shouldMapAllFields() {
        // given
        SkillEntity skill1 = new SkillEntity();
        skill1.setName("Java");
        SkillEntity skill2 = new SkillEntity();
        skill2.setName("Spring");

        EmployeeProfileSkillEntity profileSkill1 = new EmployeeProfileSkillEntity();
        profileSkill1.setSkill(skill1);
        EmployeeProfileSkillEntity profileSkill2 = new EmployeeProfileSkillEntity();
        profileSkill2.setSkill(skill2);

        GradeEntity grade1 = new GradeEntity();
        grade1.setName("Senior");
        GradeEntity grade2 = new GradeEntity();
        grade2.setName("Lead");

        EmployeeProfileEntity entity = new EmployeeProfileEntity();
        entity.setId(1L);
        entity.setName("Java Developer");
        entity.setVersion(3L);
        entity.setRatePerHour(new BigDecimal("2500.00"));
        entity.setProfileSkills(Set.of(profileSkill1, profileSkill2));
        entity.setGrades(Set.of(grade1, grade2));

        // when
        EmployeeProfileDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Java Developer");
        assertThat(dto.getVersion()).isEqualTo(3L);
        assertThat(dto.getRatePerHour()).isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(dto.getSkills()).containsExactlyInAnyOrder("Java", "Spring");
        assertThat(dto.getGrades()).containsExactlyInAnyOrder("Senior", "Lead");
    }

    @Test
    void fromEntity_shouldHandleNullCollections() {
        // given
        EmployeeProfileEntity entity = new EmployeeProfileEntity();
        entity.setId(2L);
        entity.setName("QA Engineer");
        entity.setVersion(1L);
        entity.setRatePerHour(null);
        entity.setProfileSkills(null);
        entity.setGrades(null);

        // when
        EmployeeProfileDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("QA Engineer");
        assertThat(dto.getVersion()).isEqualTo(1L);
        assertThat(dto.getRatePerHour()).isNull();
        assertThat(dto.getSkills()).isEmpty();
        assertThat(dto.getGrades()).isEmpty();
    }

    @Test
    void fromEntity_shouldHandleEmptyCollections() {
        // given
        EmployeeProfileEntity entity = new EmployeeProfileEntity();
        entity.setId(3L);
        entity.setName("DevOps");
        entity.setVersion(1L);
        entity.setRatePerHour(new BigDecimal("3000.00"));
        entity.setProfileSkills(Set.of());
        entity.setGrades(Set.of());

        // when
        EmployeeProfileDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.getSkills()).isEmpty();
        assertThat(dto.getGrades()).isEmpty();
    }

    @Test
    void toEntity_shouldMapBasicFieldsAndIgnoreRelations() {
        // given
        EmployeeProfileDto dto = new EmployeeProfileDto();
        dto.setId(10L);
        dto.setName("Senior Developer");
        dto.setVersion(5L);
        dto.setRatePerHour(new BigDecimal("4000.00"));
        dto.setSkills(Set.of("Java", "Kotlin"));
        dto.setGrades(Set.of("Senior"));

        // when
        EmployeeProfileEntity entity = mapper.toEntity(dto);

        // then
        assertThat(entity.getId()).isNull();          // игнорируется
        assertThat(entity.getName()).isEqualTo("Senior Developer");
        assertThat(entity.getVersion()).isNull();     // игнорируется
        assertThat(entity.getRatePerHour()).isEqualByComparingTo(new BigDecimal("4000.00"));
        assertThat(entity.getProfileSkills()).isNull(); // игнорируется
        assertThat(entity.getGrades()).isNull();        // игнорируется
        assertThat(entity.getEmployee()).isNull();      // игнорируется
    }

    @Test
    void mergeToEntity_shouldUpdateBasicFields() {
        // given
        EmployeeProfileEntity existing = new EmployeeProfileEntity();
        existing.setId(20L);
        existing.setName("Old Name");
        existing.setVersion(1L);
        existing.setRatePerHour(new BigDecimal("1000.00"));
        existing.setProfileSkills(new HashSet<>());
        existing.setGrades(new HashSet<>());

        EmployeeProfileDto dto = new EmployeeProfileDto();
        dto.setName("New Name");
        dto.setRatePerHour(new BigDecimal("2000.00"));
        dto.setSkills(Set.of("Java"));
        dto.setGrades(Set.of("Middle"));

        // when
        mapper.mergeToEntity(dto, existing);

        // then
        assertThat(existing.getId()).isEqualTo(20L);          // не изменился
        assertThat(existing.getName()).isEqualTo("New Name");
        assertThat(existing.getRatePerHour()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(existing.getProfileSkills()).isEmpty();    // игнорируется (не изменился)
        assertThat(existing.getGrades()).isEmpty();           // игнорируется (не изменился)
    }

    @Test
    void mergeToEntity_shouldHandleNullRatePerHour() {
        // given
        EmployeeProfileEntity existing = new EmployeeProfileEntity();
        existing.setId(30L);
        existing.setName("Existing");
        existing.setRatePerHour(new BigDecimal("500.00"));

        EmployeeProfileDto dto = new EmployeeProfileDto();
        dto.setName("Existing");
        dto.setRatePerHour(null);

        // when
        mapper.mergeToEntity(dto, existing);

        // then
        assertThat(existing.getRatePerHour()).isNull();
    }
}