package ru.kryuch.krtg.searcher.mapper.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeProfileDto;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentGradeEntity;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSkillEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileEntity;
import ru.kryuch.krtg.searcher.repository.development.DevelopmentGradeRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

class EmployeeProfileMapperTest {

    private final EmployeeProfileMapper mapper = Mappers.getMapper(EmployeeProfileMapper.class);

    @BeforeEach
    void setUp() {
        // toEntity() вызывает getGrades(dto) -> gradeRepository.findAllById(...)
        // Mappers.getMapper() не инжектит @Autowired-поля, поэтому подставляем мок вручную.
        DevelopmentGradeRepository developmentGradeRepository = Mockito.mock(DevelopmentGradeRepository.class);
        when(developmentGradeRepository.findAllById(anyCollection())).thenReturn(List.of());

        // fromEntity() вызывает employeeProfileProjectMapper.fromEntityList(entity.getProjects())
        // без мока это поле null -> NPE
        EmployeeProfileProjectMapper projectMapper = Mockito.mock(EmployeeProfileProjectMapper.class);
        when(projectMapper.fromEntityList(anyCollection())).thenReturn(List.of());

        ReflectionTestUtils.setField(mapper, "gradeRepository", developmentGradeRepository);
        ReflectionTestUtils.setField(mapper, "employeeProfileProjectMapper", projectMapper);
    }

    @Test
    void fromEntity_shouldMapAllFields() {
        // given
        DevelopmentSkillEntity skill1 = new DevelopmentSkillEntity();
        skill1.setId(1L);
        skill1.setName("Java");
        DevelopmentSkillEntity skill2 = new DevelopmentSkillEntity();
        skill2.setId(2L);
        skill2.setName("Spring");

        DevelopmentGradeEntity grade1 = new DevelopmentGradeEntity();
        grade1.setId(10L);
        grade1.setName("Senior");
        DevelopmentGradeEntity grade2 = new DevelopmentGradeEntity();
        grade2.setId(11L);
        grade2.setName("Lead");

        EmployeeProfileEntity entity = new EmployeeProfileEntity();
        entity.setId(1L);
        entity.setName("Java Developer");
        entity.setRatePerHour(new BigDecimal("2500.00"));
        entity.setSkills(Set.of(skill1, skill2));
        entity.setGrades(Set.of(grade1, grade2));

        // when
        EmployeeProfileDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Java Developer");
        assertThat(dto.getRatePerHour()).isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(dto.getSkillIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(dto.getGradeIds()).containsExactlyInAnyOrder(10L, 11L);
        assertThat(dto.getProjects()).isEmpty();
    }

    @Test
    void fromEntity_shouldHandleNullCollections() {
        // given
        EmployeeProfileEntity entity = new EmployeeProfileEntity();
        entity.setId(2L);
        entity.setName("QA Engineer");
        entity.setRatePerHour(null);
        entity.setSkills(null);
        entity.setGrades(null);

        // when
        EmployeeProfileDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("QA Engineer");
        assertThat(dto.getRatePerHour()).isNull();
        assertThat(dto.getSkillIds()).isEmpty();
        assertThat(dto.getGradeIds()).isEmpty();
    }

    @Test
    void fromEntity_shouldHandleEmptyCollections() {
        // given
        EmployeeProfileEntity entity = new EmployeeProfileEntity();
        entity.setId(3L);
        entity.setName("DevOps");
        entity.setRatePerHour(new BigDecimal("3000.00"));
        entity.setSkills(Set.of());
        entity.setGrades(Set.of());

        // when
        EmployeeProfileDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.getSkillIds()).isEmpty();
        assertThat(dto.getGradeIds()).isEmpty();
    }

    @Test
    void toEntity_shouldMapBasicFieldsAndIgnoreRelations() {
        // given
        EmployeeProfileDto dto = new EmployeeProfileDto();
        dto.setId(10L);
        dto.setName("Senior Developer");
        dto.setRatePerHour(new BigDecimal("4000.00"));
        dto.setSkillIds(Set.of(1L, 2L));
        dto.setGradeIds(Set.of(10L));

        // when
        EmployeeProfileEntity entity = mapper.toEntity(dto);

        // then
        assertThat(entity.getId()).isNull();          // игнорируется
        assertThat(entity.getName()).isEqualTo("Senior Developer");
        assertThat(entity.getRatePerHour()).isEqualByComparingTo(new BigDecimal("4000.00"));
        assertThat(entity.getSkills()).isNull();      // маппинг skillIds -> skills не задан, поле остаётся null
        // grades заполняется через getGrades(dto) -> мок вернул пустой List
        assertThat(entity.getGrades()).isEmpty();
        assertThat(entity.getEmployee()).isNull();    // игнорируется
    }

    @Test
    void mergeToEntity_shouldUpdateBasicFields() {
        // given
        EmployeeProfileEntity existing = new EmployeeProfileEntity();
        existing.setId(20L);
        existing.setName("Old Name");
        existing.setRatePerHour(new BigDecimal("1000.00"));
        existing.setSkills(new HashSet<>());
        existing.setGrades(new HashSet<>());

        EmployeeProfileDto dto = new EmployeeProfileDto();
        dto.setName("New Name");
        dto.setRatePerHour(new BigDecimal("2000.00"));
        dto.setSkillIds(Set.of(1L));
        dto.setGradeIds(Set.of(10L));

        // when
        mapper.mergeToEntity(dto, existing);

        // then
        assertThat(existing.getId()).isEqualTo(20L);          // не изменился
        assertThat(existing.getName()).isEqualTo("New Name");
        assertThat(existing.getRatePerHour()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(existing.getSkills()).isEmpty();           // игнорируется (не изменился)
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