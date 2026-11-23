package ru.kryuch.krtg.searcher.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.kryuch.krtg.searcher.dto.EmployeeDto;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeContactEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeLanguageEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileProjectEntity;
import ru.kryuch.krtg.searcher.mapper.EmployeeMapper;
import ru.kryuch.krtg.searcher.repository.GradeRepository;
import ru.kryuch.krtg.searcher.repository.location.LocationCityRepository;
import ru.kryuch.krtg.searcher.repository.location.LocationLanguageRepository;
import ru.kryuch.krtg.searcher.repository.location.LocationStateRepository;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmployeeHelper {

    private final EmployeeMapper employeeMapper;
    private final LocationStateRepository locationStateRepository;
    private final LocationCityRepository locationCityRepository;
    private final LocationLanguageRepository locationLanguageRepository;

    public EmployeeEntity dtoToEntity(EmployeeDto dto) {
        EmployeeEntity entity = employeeMapper.toEntity(dto);
        entity.setCitizenship(locationStateRepository.findByCode(dto.getCitizenship()).orElse(null));
        entity.setCity(locationCityRepository.findByNameAndStateId(dto.getCity(), entity.getCitizenship().getId()).orElse(null));
        entity.setContacts(dto.getContacts().stream().map(c -> {
            EmployeeContactEntity contact = new EmployeeContactEntity();
            contact.setEmployee(entity);
            contact.setType(c.getType().name());
            contact.setValue(c.getValue());
            return contact;
        }).collect(Collectors.toSet()));
        entity.setLanguages(dto.getLanguages().stream().map(l -> {
            EmployeeLanguageEntity lang = new EmployeeLanguageEntity();
            lang.setEmployee(entity);
            lang.setLanguage(locationLanguageRepository.findByCode(l.getLanguage()).orElseThrow());
            lang.setLevel(l.getLevel());
            return lang;
        }).collect(Collectors.toSet()));

        if (!CollectionUtils.isEmpty(entity.getProfiles())) {
            for (EmployeeProfileEntity profile : entity.getProfiles()) {
                profile.setEmployee(entity);
                if (!CollectionUtils.isEmpty(profile.getProjects())) {
                    for (EmployeeProfileProjectEntity project : profile.getProjects()) {
                        project.setProfile(profile);
                    }
                }
            }
        }

        return entity;
    }

}
