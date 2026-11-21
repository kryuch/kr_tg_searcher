package ru.kryuch.krtg.searcher.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.kryuch.krtg.searcher.dto.EmployeeDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeContactDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeLanguage;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeContactEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeLanguageEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationCityEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationLanguageEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationStateEntity;
import ru.kryuch.krtg.searcher.type.ContactType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmployeeMapperImpl.class})
class EmployeeMapperTest {

    @Autowired
    private EmployeeMapper mapper;

    @Test
    void fromEntity_shouldMapAllFields() {
        // given
        LocationStateEntity state = new LocationStateEntity();
        state.setName("Россия");

        LocationCityEntity city = new LocationCityEntity();
        city.setName("Москва");
        city.setState(state);

        LocationLanguageEntity language = new LocationLanguageEntity();
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
        assertThat(dto.getCitizenship()).isEqualTo("Россия");
        assertThat(dto.getCity()).isEqualTo("Москва");
        assertThat(dto.getState()).isEqualTo("Россия");
        assertThat(dto.getContacts()).hasSize(1);
        assertThat(dto.getContacts().get(0).getValue()).isEqualTo("+7 999 123-45-67");
        assertThat(dto.getContacts().get(0).getType()).isEqualTo(ContactType.PHONE);
        assertThat(dto.getLanguages()).hasSize(1);
        assertThat(dto.getLanguages().get(0).getLanguage()).isEqualTo("русский");
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
        assertThat(dto.getCitizenship()).isNull();
        assertThat(dto.getCity()).isNull();
        assertThat(dto.getState()).isNull();
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
            .citizenship("Россия")
            .city("Москва")
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

    private EmployeeLanguage createLanguage(String name, String level) {
        EmployeeLanguage lang = new EmployeeLanguage();
        lang.setLanguage(name);
        lang.setLevel(level);
        return lang;
    }
}