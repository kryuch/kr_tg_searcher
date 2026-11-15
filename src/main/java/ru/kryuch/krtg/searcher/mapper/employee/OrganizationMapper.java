package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;

import ru.kryuch.krtg.searcher.dto.employee.OrganizationDto;
import ru.kryuch.krtg.searcher.entity.employee.OrganizationEntity;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    OrganizationEntity toEntity(OrganizationDto dto);
    OrganizationDto toDto(OrganizationEntity entity);
}
