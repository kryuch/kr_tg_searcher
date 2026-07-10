package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.IgnoreInfo;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.entity.IgnoreEntity;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;
import ru.kryuch.krtg.searcher.integration.dto.ChatResponse;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.repository.TgAccountRepository;
import ru.kryuch.krtg.searcher.type.SendMessageStatus;
import ru.kryuch.krtg.searcher.util.SendResult;

import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ChatMapper implements TMapper <ChatEntity, ChatInfo> {

    @Autowired
    TgAccountRepository tgAccountRepository;

    @Mapping(target = "status", source = "status.type")
    public abstract ChatEntity toEntity(ChatInfo info);

    @Mapping(target = "status", expression = "java(ChatStatus.getChatStatus(entity.getStatus()))")
    public abstract ChatInfo fromEntity(ChatEntity entity);


    public abstract ChatInfo fromResponse(ChatResponse response);

    public abstract List <ChatInfo> fromResponse(List <ChatResponse> response);

    @Mapping(target = "name", source = "username")
    @Mapping(target = "id", source = "numericId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sendStatus", expression = "java(getSendStatus(result))")
    @Mapping(target = "comment", source = "error")
    public abstract ChatInfo fromSendResult(SendResult result);

    protected SendMessageStatus getSendStatus(SendResult result) {
        if (result.getStatus().equals("skipped")) return SendMessageStatus.SKIP;
        if (result.getStatus().equals("error")) return SendMessageStatus.ERROR;
        return SendMessageStatus.SUCCESS;
    }

    @AfterMapping
     protected void fromResponse(@MappingTarget ChatInfo chatInfo, ChatResponse response) {
         chatInfo.setTgAccount(tgAccountRepository.findById(response.getTgAccountId()).orElse(new TgAccountEntity()).getDescription());
     }
}