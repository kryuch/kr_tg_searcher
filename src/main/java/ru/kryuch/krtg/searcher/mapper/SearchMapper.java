package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;
import ru.kryuch.krtg.searcher.integration.dto.SearchRequest;
import ru.kryuch.krtg.searcher.repository.TgAccountRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class SearchMapper {

    @Autowired
    TgAccountRepository tgAccountRepository;

    @Autowired
    TgAccountMapper tgAccountMapper;

    @Mapping(target = "tgAccounts", source = "tgAccountIds")
    public abstract SearchRequest toRequest(SearchParams params);
/*
    protected List<TgAccountInfo> getTgAccounts(List<Integer> ids) {
        Iterable<TgAccountEntity> entities = tgAccountRepository.findAllById(ids);
        return StreamSupport.stream(entities.spliterator(), false)
                .map(tgAccountMapper::fromEntity)
                .collect(Collectors.toList());
    }*/
}
