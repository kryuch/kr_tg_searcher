package ru.kryuch.krtg.searcher.service.education;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.education.EducationTypeDto;
import ru.kryuch.krtg.searcher.entity.education.EducationTypeEntity;
import ru.kryuch.krtg.searcher.mapper.education.EducationTypeMapper;
import ru.kryuch.krtg.searcher.repository.education.EducationTypeRepository;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Service
@Transactional(readOnly = true)
public class EducationTypeService extends AbstractService <Long, EducationTypeEntity, EducationTypeDto, EducationTypeMapper, EducationTypeRepository> {
    public EducationTypeService(final EducationTypeRepository repository, final EducationTypeMapper mapper) {
        super(repository, mapper, "EducationType");
    }
}
