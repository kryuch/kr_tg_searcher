package ru.kryuch.krtg.searcher.mapper.location;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.location.LocationLanguageDto;
import ru.kryuch.krtg.searcher.entity.location.LocationLanguageEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class LocationLanguageMapper implements TMapper<LocationLanguageEntity, LocationLanguageDto> {
}