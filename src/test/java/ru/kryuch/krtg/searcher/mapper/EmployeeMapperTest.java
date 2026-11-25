package ru.kryuch.krtg.searcher.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ru.kryuch.krtg.searcher.dto.EmployeeDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeContactDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeLanguageDto;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeContactEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeLanguageEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationCityEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationLanguageEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationStateEntity;
import ru.kryuch.krtg.searcher.mapper.employee.EmployeeProfileMapper;
import ru.kryuch.krtg.searcher.type.ContactType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    private EmployeeMapper mapper;

    @BeforeEach
    void setUp() {
        // Мокаем вложенный мапер: EmployeeMapper не должен тянуть за собой
        // EmployeeProfileMapperImpl и его зависимости (GradeRepository и т.д.)
        EmployeeProfileMapper employeeProfileMapper = Mockito.mock(EmployeeProfileMapper.class);

        EmployeeMapperImpl impl = new EmployeeMapperImpl();
        ReflectionTestUtils.setField(impl, "employeeProfileMapper", employeeProfileMapper);

        mapper = impl;
    }

    @Test
    void fromEntity_shouldMapAllFields() {
        // given
        LocationStateEntity state = new LocationStateEntity();
        state.setId(7L);
        state.setName("Россия");

        LocationCityEntity city = new LocationCityEntity();
        city.setId(68L);
        city.setName("Москва");
        city.setState(state);

        LocationLanguageEntity language = new LocationLanguageEntity();
        language.setId(7L);
        language.setName("русский");

        EmployeeLanguageEntity languageEntity = new EmployeeLanguageEntity();
        languageEntity.setLanguage(language);
        languageEntity.setLevel("C2");

        EmployeeContactEntity contact = new EmployeeContactEntity();
        contact.setType(ContactType.PHONE.name());
        contact.setValue("+7 999 123-45-67");

        EmployeeEntity entity = new EmployeeEntity();
        entity.setLastName("Иванов");
        entity.setFirstName("Иван");
        entity.setMiddleName("Иванович");
        entity.setBirthDate(LocalDate.of(1990, 1, 1));
        entity.setIsMale(true);
        entity.setCitizenship(state);
        entity.setCity(city);
        entity.setContacts(Set.of(contact));
        entity.setLanguages(Set.of(languageEntity));

        // when
        EmployeeDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.getLastName()).isEqualTo("Иванов");
        assertThat(dto.getFirstName()).isEqualTo("Иван");
        assertThat(dto.getMiddleName()).isEqualTo("Иванович");
        assertThat(dto.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(dto.isMale()).isTrue();
        assertThat(dto.getGender()).isEqualTo("Мужской");
        assertThat(dto.getCitizenshipId()).isEqualTo(7L);
        assertThat(dto.getCityId()).isEqualTo(68L);
        assertThat(dto.getStateId()).isEqualTo(7L);
        assertThat(dto.getContacts()).hasSize(1);
        assertThat(dto.getContacts().get(0).getValue()).isEqualTo("+7 999 123-45-67");
        assertThat(dto.getContacts().get(0).getType()).isEqualTo(ContactType.PHONE);
        assertThat(dto.getLanguages()).hasSize(1);
        assertThat(dto.getLanguages().get(0).getLanguageId()).isEqualTo(7L);
        assertThat(dto.getLanguages().get(0).getLevel()).isEqualTo("C2");
    }

    @Test
    void fromEntity_shouldHandleNullFields() {
        // given
        EmployeeEntity entity = new EmployeeEntity();
        entity.setLastName("Петров");
        entity.setFirstName("Пётр");
        entity.setMiddleName("Петрович");
        entity.setBirthDate(LocalDate.of(1985, 5, 5));
        entity.setIsMale(null);
        entity.setCitizenship(null);
        entity.setCity(null);
        entity.setContacts(null);
        entity.setLanguages(null);

        // when
        EmployeeDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.getLastName()).isEqualTo("Петров");
        assertThat(dto.isMale()).isFalse();
        assertThat(dto.getGender()).isNull();
        assertThat(dto.getCitizenshipId()).isNull();
        assertThat(dto.getCityId()).isNull();
        assertThat(dto.getStateId()).isNull();
        assertThat(dto.getContacts()).isEmpty();
        assertThat(dto.getLanguages()).isEmpty();
    }

    @Test
    void fromEntity_shouldMapFemaleGender() {
        // given
        EmployeeEntity entity = new EmployeeEntity();
        entity.setLastName("Сидорова");
        entity.setFirstName("Анна");
        entity.setMiddleName("Сидоровна");
        entity.setBirthDate(LocalDate.of(1995, 3, 10));
        entity.setIsMale(false);
        entity.setContacts(Set.of());
        entity.setLanguages(Set.of());

        // when
        EmployeeDto dto = mapper.fromEntity(entity);

        // then
        assertThat(dto.isMale()).isFalse();
        assertThat(dto.getGender()).isEqualTo("Женский");
    }

    @Test
    void toEntity_shouldMapBasicFieldsAndIgnoreRelations() {
        // given
        EmployeeDto dto = EmployeeDto.builder()
            .lastName("Иванов")
            .firstName("Иван")
            .middleName("Иванович")
            .birthDate(LocalDate.of(1990, 1, 1))
            .isMale(true)
            .citizenshipId(7L)
            .cityId(68L)
            .contacts(List.of(new EmployeeContactDto("+7 999 123-45-67", ContactType.PHONE)))
            .languages(List.of(createLanguage("русский", "C2")))
            .build();

        // when
        EmployeeEntity entity = mapper.toEntity(dto);

        // then
        assertThat(entity.getLastName()).isEqualTo("Иванов");
        assertThat(entity.getFirstName()).isEqualTo("Иван");
        assertThat(entity.getMiddleName()).isEqualTo("Иванович");
        assertThat(entity.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(entity.getIsMale()).isTrue();
        assertThat(entity.getCitizenship()).isNull();   // игнорируется
        assertThat(entity.getCity()).isNull();          // игнорируется
        assertThat(entity.getContacts()).isEmpty();
        assertThat(entity.getLanguages()).isEmpty();
    }

    @Test
    void mergeToEntity_shouldUpdateBasicFields() {
        // given
        EmployeeEntity existing = new EmployeeEntity();
        existing.setLastName("Старое");
        existing.setFirstName("Старое");
        existing.setMiddleName("Старое");
        existing.setBirthDate(LocalDate.of(1900, 1, 1));
        existing.setIsMale(false);

        EmployeeDto dto = EmployeeDto.builder()
            .lastName("Новое")
            .firstName("Новое")
            .middleName("Новое")
            .birthDate(LocalDate.of(2000, 1, 1))
            .isMale(true)
            .build();

        // when
        mapper.mergeToEntity(dto, existing);

        // then
        assertThat(existing.getLastName()).isEqualTo("Новое");
        assertThat(existing.getFirstName()).isEqualTo("Новое");
        assertThat(existing.getMiddleName()).isEqualTo("Новое");
        assertThat(existing.getBirthDate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(existing.getIsMale()).isTrue();
    }

    private EmployeeLanguageDto createLanguage(String name, String level) {
        EmployeeLanguageDto lang = new EmployeeLanguageDto();
        lang.setLanguageId(7L);
        lang.setLevel(level);
        return lang;
    }
}