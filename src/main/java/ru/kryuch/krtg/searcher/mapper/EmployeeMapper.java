package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.EmployeeDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeContactDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeLanguageDto;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeContactEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeLanguageEntity;
import ru.kryuch.krtg.searcher.mapper.employee.EmployeeProfileMapper;
import ru.kryuch.krtg.searcher.type.ContactType;

import java.util.Collection;
import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {EmployeeProfileMapper.class}
)
public abstract class EmployeeMapper implements TMapper<EmployeeEntity, EmployeeDto> {

    @Override
    @Mapping(target = "gender", ignore = true)          // вычисляется в @AfterMapping
    @Mapping(target = "isMale", source = "isMale")      // Boolean -> boolean
    @Mapping(target = "citizenshipId", source = "citizenship.id")
    @Mapping(target = "cityId", source = "city.id")
    @Mapping(target = "stateId", source = "city.state.id")
    @Mapping(target = "contacts", source = "contacts", qualifiedByName = "contactsToDto")
    @Mapping(target = "languages", source = "languages", qualifiedByName = "languagesToDto")
    public abstract EmployeeDto fromEntity(EmployeeEntity entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isMale", source = "male")
    @Mapping(target = "citizenship", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "languages", ignore = true)
    public abstract EmployeeEntity toEntity(EmployeeDto dto);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isMale", source = "male")
    @Mapping(target = "citizenship", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "languages", ignore = true)
    public abstract EmployeeEntity mergeToEntity(EmployeeDto dto, @MappingTarget EmployeeEntity entity);

    // ==================== Вспомогательные методы ====================

    @Named("contactsToDto")
    protected List<EmployeeContactDto> contactsToDto(Collection<EmployeeContactEntity> contacts) {
        if (contacts == null) {
            return List.of();
        }
        return contacts.stream()
            .map(c -> new EmployeeContactDto(c.getValue(), parseContactType(c.getType())))
            .toList();
    }

    @Named("languagesToDto")
    protected List<EmployeeLanguageDto> languagesToDto(Collection<EmployeeLanguageEntity> languages) {
        if (languages == null) {
            return List.of();
        }
        return languages.stream()
            .map(l -> {
                EmployeeLanguageDto dto = new EmployeeLanguageDto();
                dto.setLanguageId(l.getLanguage().getId());
                dto.setLevel(l.getLevel());
                return dto;
            })
            .toList();
    }

    private ContactType parseContactType(String type) {
        if (type == null) {
            return ContactType.OTHER;
        }
        try {
            return ContactType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return ContactType.OTHER;
        }
    }

    // ==================== Пост-обработка (работает с билдером) ====================

    @AfterMapping
    protected void fillGender(EmployeeEntity entity, @MappingTarget EmployeeDto.EmployeeDtoBuilder builder) {
        if (entity.getIsMale() == null) {
            builder.gender(null);
        } else if (entity.getIsMale()) {
            builder.gender("Мужской");
        } else {
            builder.gender("Женский");
        }
        // синхронизируем isMale (на случай, если MapStruct не замапил Boolean -> boolean)
        builder.isMale(Boolean.TRUE.equals(entity.getIsMale()));
    }
}