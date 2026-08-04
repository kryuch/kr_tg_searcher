package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.ChatKey;
import ru.kryuch.krtg.searcher.projection.ChatKeyProjection;

import java.util.Collection;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ChatKeyMapper {
    public abstract List<ChatKey> fromEntityList(Collection<ChatKeyProjection> entity);

    public abstract ChatKey fromEntity(ChatKeyProjection entity);


}