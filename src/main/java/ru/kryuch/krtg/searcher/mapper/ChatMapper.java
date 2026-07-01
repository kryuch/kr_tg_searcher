package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.type.SendMessageStatus;
import ru.kryuch.krtg.searcher.util.SendResult;

import java.util.Collection;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ChatMapper {

    @Mapping(target = "status", source = "status.type")
    public abstract ChatEntity toEntity(ChatInfo info);

    @Mapping(target = "status", expression = "java(ChatStatus.getChatStatus(entity.getStatus()))")
    public abstract ChatInfo fromEntity(ChatEntity entity);

    public abstract List<ChatEntity> toEntityList(Collection<ChatInfo> info);

    @Mapping(target = "name", source = "username")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sendStatus", expression = "java(getSendStatus(result))")
    @Mapping(target = "comment", source = "error")
    public abstract ChatInfo fromSendResult(SendResult result);

    protected SendMessageStatus getSendStatus(SendResult result) {
        if (result.getStatus().equals("skipped")) return SendMessageStatus.SKIP;
        if (result.getStatus().equals("error")) return SendMessageStatus.ERROR;
        return SendMessageStatus.SUCCESS;
    }
}