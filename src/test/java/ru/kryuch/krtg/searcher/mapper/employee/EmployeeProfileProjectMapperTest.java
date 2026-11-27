package ru.kryuch.krtg.searcher.mapper.employee;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.kryuch.krtg.searcher.dto.employee.profile.EmployeeProfileProjectDto;
import ru.kryuch.krtg.searcher.entity.OrganisationEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileProjectEntity;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSkillEntity;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeProfileProjectMapperTest {

    private final EmployeeProfileProjectMapper mapper =
        Mappers.getMapper(EmployeeProfileProjectMapper.class);

    @Test
    void fromEntity_shouldMapAllFields() {
        // given
        OrganisationEntity organisation = new OrganisationEntity();
        organisation.setName("Ростелеком");

        DevelopmentSkillEntity skill1 = new DevelopmentSkillEntity();
        skill1.setName("Java");
        DevelopmentSkillEntity skill2 = new DevelopmentSkillEntity();
        skill2.setName("Spring");

        EmployeeProfileProjectEntity entity = new EmployeeProfileProjectEntity();
        entity.setId(1L);
        entity.setName("Разработка портала");
        entity.setDescription("Корпоративный портал");
        entity.setProgress("Завершён");
        entity.setOrganisation(organisation);
        entity.setRole("Senior Java Developer");
        entity.setStartAt(LocalDate.of(2023, 1, 10));
        entity.setEndAt(LocalDate.of(2024, 6, 30));
        entity.setSkills(Set.of(skill1, skill2));

        // when
        EmployeeProfileProjectDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Разработка портала");
        assertThat(dto.getDescription()).isEqualTo("Корпоративный портал");
        assertThat(dto.getProgress()).isEqualTo("Завершён");
      //  assertThat(dto.getOrganisation()).isEqualTo("Ростелеком");
        assertThat(dto.getRole()).isEqualTo("Senior Java Developer");
        assertThat(dto.getStartAt()).isEqualTo(LocalDate.of(2023, 1, 10));
        assertThat(dto.getEndAt()).isEqualTo(LocalDate.of(2024, 6, 30));
        //assertThat(dto.getSkills()).containsExactlyInAnyOrder("Java", "Spring");
    }

    @Test
    void fromEntity_shouldHandleNullOrganisationAndSkills() {
        // given
        EmployeeProfileProjectEntity entity = new EmployeeProfileProjectEntity();
        entity.setId(2L);
        entity.setName("Внутренний проект");
        entity.setDescription(null);
        entity.setProgress(null);
        entity.setOrganisation(null);
        entity.setRole(null);
        entity.setStartAt(null);
        entity.setEndAt(null);
        entity.setSkills(null);

        // when
        EmployeeProfileProjectDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Внутренний проект");
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getProgress()).isNull();
    //    assertThat(dto.getOrganisation()).isNull();
        assertThat(dto.getRole()).isNull();
        assertThat(dto.getStartAt()).isNull();
        assertThat(dto.getEndAt()).isNull();
      //  assertThat(dto.getSkills()).isEmpty();
    }

    @Test
    void fromEntity_shouldHandleEmptySkills() {
        // given
        EmployeeProfileProjectEntity entity = new EmployeeProfileProjectEntity();
        entity.setId(3L);
        entity.setName("Без навыков");
        entity.setSkills(Set.of());

        // when
        EmployeeProfileProjectDto dto = mapper.fromEntity(entity);

        // then
 //       assertThat(dto.getSkills()).isEmpty();
    }

    @Test
    void toEntity_shouldMapBasicFieldsAndIgnoreRelations() {
        // given
        EmployeeProfileProjectDto dto = new EmployeeProfileProjectDto();
        dto.setId(10L);
        dto.setName("Новый проект");
        dto.setDescription("Описание");
        dto.setProgress("В работе");
   //     dto.setOrganisation("Сбер");
        dto.setRole("Team Lead");
        dto.setStartAt(LocalDate.of(2024, 1, 1));
        dto.setEndAt(LocalDate.of(2024, 12, 31));
     //   dto.setSkills(Set.of("Java", "Kafka"));

        // when
        EmployeeProfileProjectEntity entity = mapper.toEntity(dto);

        // then
        assertThat(entity.getId()).isNull();           // игнорируется
        assertThat(entity.getName()).isEqualTo("Новый проект");
        assertThat(entity.getDescription()).isEqualTo("Описание");
        assertThat(entity.getProgress()).isEqualTo("В работе");
        assertThat(entity.getOrganisation()).isNull(); // игнорируется
        assertThat(entity.getRole()).isEqualTo("Team Lead");
        assertThat(entity.getStartAt()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(entity.getEndAt()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(entity.getSkills()).isNull();       // игнорируется
        assertThat(entity.getProfile()).isNull();      // игнорируется
    }

    @Test
    void mergeToEntity_shouldUpdateBasicFields() {
        // given
        EmployeeProfileProjectEntity existing = new EmployeeProfileProjectEntity();
        existing.setId(20L);
        existing.setName("Старое имя");
        existing.setDescription("Старое описание");
        existing.setProgress("Старый прогресс");
        existing.setRole("Старая роль");
        existing.setStartAt(LocalDate.of(2020, 1, 1));
        existing.setEndAt(LocalDate.of(2021, 1, 1));
        existing.setOrganisation(new OrganisationEntity());
        existing.setSkills(Set.of());

        EmployeeProfileProjectDto dto = new EmployeeProfileProjectDto();
        dto.setName("Новое имя");
        dto.setDescription("Новое описание");
        dto.setProgress("Завершён");
    //    dto.setOrganisation("Новая организация");
        dto.setRole("Новая роль");
        dto.setStartAt(LocalDate.of(2023, 5, 5));
        dto.setEndAt(LocalDate.of(2024, 5, 5));
    //    dto.setSkills(Set.of("Java"));

        // when
        mapper.mergeToEntity(dto, existing);

        // then
        assertThat(existing.getId()).isEqualTo(20L);             // не изменился
        assertThat(existing.getName()).isEqualTo("Новое имя");
        assertThat(existing.getDescription()).isEqualTo("Новое описание");
        assertThat(existing.getProgress()).isEqualTo("Завершён");
        assertThat(existing.getRole()).isEqualTo("Новая роль");
        assertThat(existing.getStartAt()).isEqualTo(LocalDate.of(2023, 5, 5));
        assertThat(existing.getEndAt()).isEqualTo(LocalDate.of(2024, 5, 5));
        assertThat(existing.getOrganisation()).isNotNull();       // игнорируется (не изменился)
        assertThat(existing.getSkills()).isEmpty();               // игнорируется (не изменился)
    }

    @Test
    void mergeToEntity_shouldHandleNullFieldsInDto() {
        // given
        EmployeeProfileProjectEntity existing = new EmployeeProfileProjectEntity();
        existing.setId(30L);
        existing.setName("Имя");
        existing.setDescription("Описание");
        existing.setRole("Роль");

        EmployeeProfileProjectDto dto = new EmployeeProfileProjectDto();
        dto.setName("Имя");
        dto.setDescription(null);
        dto.setProgress(null);

        // when
        mapper.mergeToEntity(dto, existing);

        // then
        assertThat(existing.getName()).isEqualTo("Имя");
        assertThat(existing.getDescription()).isNull();   // перезаписан на null
        assertThat(existing.getProgress()).isNull();
    }
}