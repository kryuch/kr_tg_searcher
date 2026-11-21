package ru.kryuch.krtg.searcher.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kryuch.krtg.searcher.dto.EmployeeDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeContactDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeLanguage;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeContactEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeLanguageEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationCityEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationLanguageEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationStateEntity;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.location.LocationCityRepository;
import ru.kryuch.krtg.searcher.repository.location.LocationLanguageRepository;
import ru.kryuch.krtg.searcher.repository.location.LocationStateRepository;
import ru.kryuch.krtg.searcher.type.ContactType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeHelperTest {

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private LocationStateRepository locationStateRepository;

    @Mock
    private LocationCityRepository locationCityRepository;

    @Mock
    private LocationLanguageRepository locationLanguageRepository;

    @InjectMocks
    private EmployeeHelper employeeHelper;

    @Test
    void dtoToEntity_shouldMapAllFields() {
        // given
        EmployeeDto dto = EmployeeDto.builder()
            .lastName("Иванов")
            .firstName("Иван")
            .middleName("Иванович")
            .birthDate(LocalDate.of(1990, 1, 1))
            .isMale(true)
            .citizenship("RU")
            .city("Москва")
            .contacts(List.of(new EmployeeContactDto("+7 999 123-45-67", ContactType.PHONE)))
            .languages(List.of(createLanguage("русский", "C2")))
            .build();

        LocationStateEntity state = new LocationStateEntity();
        state.setId(1L);
        state.setCode("RU");
        state.setName("Россия");

        LocationCityEntity city = new LocationCityEntity();
        city.setId(10L);
        city.setName("Москва");
        city.setState(state);

        LocationLanguageEntity language = new LocationLanguageEntity();
        language.setId(100L);
        language.setName("русский");

        EmployeeEntity mappedEntity = new EmployeeEntity();
        mappedEntity.setLastName("Иванов");
        mappedEntity.setFirstName("Иван");
        mappedEntity.setMiddleName("Иванович");
        mappedEntity.setBirthDate(LocalDate.of(1990, 1, 1));
        mappedEntity.setIsMale(true);

        when(employeeMapper.toEntity(dto)).thenReturn(mappedEntity);
        when(locationStateRepository.findByCode("RU")).thenReturn(Optional.of(state));
        when(locationCityRepository.findByNameAndStateId("Москва", 1L)).thenReturn(Optional.of(city));
        when(locationLanguageRepository.findByName("русский")).thenReturn(Optional.of(language));

        // when
        EmployeeEntity result = employeeHelper.dtoToEntity(dto);

        // then
        assertThat(result.getCitizenship()).isEqualTo(state);
        assertThat(result.getCity()).isEqualTo(city);
        assertThat(result.getContacts()).hasSize(1);
        EmployeeContactEntity contact = result.getContacts().iterator().next();
        assertThat(contact.getEmployee()).isEqualTo(result);
        assertThat(contact.getType()).isEqualTo(ContactType.PHONE.name());
        assertThat(contact.getValue()).isEqualTo("+7 999 123-45-67");
        assertThat(result.getLanguages()).hasSize(1);
        EmployeeLanguageEntity lang = result.getLanguages().iterator().next();
        assertThat(lang.getEmployee()).isEqualTo(result);
        assertThat(lang.getLanguage()).isEqualTo(language);
        assertThat(lang.getLevel()).isEqualTo("C2");
    }

    @Test
    void dtoToEntity_shouldHandleNullCitizenship() {
        // given
        EmployeeDto dto = EmployeeDto.builder()
            .lastName("Иванов")
            .firstName("Иван")
            .middleName("Иванович")
            .birthDate(LocalDate.of(1990, 1, 1))
            .isMale(true)
            .citizenship("XX")
            .city("Москва")
            .contacts(List.of())
            .languages(List.of())
            .build();

        EmployeeEntity mappedEntity = new EmployeeEntity();
        when(employeeMapper.toEntity(dto)).thenReturn(mappedEntity);
        when(locationStateRepository.findByCode("XX")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> employeeHelper.dtoToEntity(dto))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void dtoToEntity_shouldHandleMissingLanguage() {
        // given
        EmployeeDto dto = EmployeeDto.builder()
            .lastName("Иванов")
            .firstName("Иван")
            .middleName("Иванович")
            .birthDate(LocalDate.of(1990, 1, 1))
            .isMale(true)
            .citizenship("RU")
            .city("Москва")
            .contacts(List.of())
            .languages(List.of(createLanguage("неизвестный", "A1")))
            .build();

        LocationStateEntity state = new LocationStateEntity();
        state.setId(1L);
        LocationCityEntity city = new LocationCityEntity();
        city.setId(10L);

        EmployeeEntity mappedEntity = new EmployeeEntity();
        when(employeeMapper.toEntity(dto)).thenReturn(mappedEntity);
        when(locationStateRepository.findByCode("RU")).thenReturn(Optional.of(state));
        when(locationCityRepository.findByNameAndStateId("Москва", 1L)).thenReturn(Optional.of(city));
        when(locationLanguageRepository.findByName("неизвестный")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> employeeHelper.dtoToEntity(dto))
            .isInstanceOf(java.util.NoSuchElementException.class);
    }

    private EmployeeLanguage createLanguage(String name, String level) {
        EmployeeLanguage lang = new EmployeeLanguage();
        lang.setLanguage(name);
        lang.setLevel(level);
        return lang;
    }
}