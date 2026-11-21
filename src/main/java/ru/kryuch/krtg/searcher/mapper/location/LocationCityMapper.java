package ru.kryuch.krtg.searcher.mapper.location;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kryuch.krtg.searcher.dto.location.LocationCityDto;
import ru.kryuch.krtg.searcher.entity.location.LocationCityEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationStateEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;
import ru.kryuch.krtg.searcher.repository.location.LocationStateRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class LocationCityMapper implements TMapper<LocationCityEntity, LocationCityDto> {

    @Autowired
    protected LocationStateRepository stateRepository;

    @Override
    @Mapping(target = "stateId", source = "state.id")
    @Mapping(target = "stateName", source = "state.name")
    public abstract LocationCityDto fromEntity(LocationCityEntity entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", source = "stateId", qualifiedByName = "toState")
    public abstract LocationCityEntity toEntity(LocationCityDto dto);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", source = "stateId", qualifiedByName = "toState")
    public abstract LocationCityEntity mergeToEntity(LocationCityDto dto, @MappingTarget LocationCityEntity entity);

    @Named("toState")
    protected LocationStateEntity toState(Long stateId) {
        return stateId == null ? null : stateRepository.getReferenceById(stateId);
    }
}