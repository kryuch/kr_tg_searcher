package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.UserFileDto;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;
import ru.kryuch.krtg.searcher.entity.UserFileEntity;
import ru.kryuch.krtg.searcher.integration.dto.ChatResponse;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        imports = LocalDateTime.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserFileMapper implements TMapper <UserFileEntity, UserFileDto> {

    @Mapping(target = "originalName", source = "originalFilename")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    public abstract UserFileEntity toEntity(MultipartFile multipartFile);

    @AfterMapping
    protected void afterToEntity(@MappingTarget UserFileEntity userFileEntity, MultipartFile multipartFile) {
        if (userFileEntity.getContentType() == null) userFileEntity.setContentType("application/octet-stream");
    }
}
