package ru.kryuch.krtg.searcher.integration.deepseek.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.entity.deepseek.DeepseekTaskEntity;
import ru.kryuch.krtg.searcher.integration.deepseek.dto.DsTask;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class DeepseekMapper {

    @Mapping(target = "value", source = "description")
    @Mapping(target = "isReady", constant = "true")
    public abstract DsTask toDto(DeepseekTaskEntity entity);

}
