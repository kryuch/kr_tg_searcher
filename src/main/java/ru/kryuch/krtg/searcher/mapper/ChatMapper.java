package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.entity.ChatEntity;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ChatMapper {

    public abstract ChatEntity fromEntity(ChatInfo info);

}