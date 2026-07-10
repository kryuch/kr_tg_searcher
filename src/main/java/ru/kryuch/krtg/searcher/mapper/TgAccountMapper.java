package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TgAccountMapper implements TMapper <TgAccountEntity, TgAccountInfo> {
}
