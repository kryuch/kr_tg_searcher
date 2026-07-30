package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.entity.TgUserEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ChatUserMapper implements TMapper<TgUserEntity, ChatInfo> {

    public abstract TgUserEntity toEntity(ChatInfo info);

}
