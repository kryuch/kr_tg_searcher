package ru.kryuch.krtg.searcher.service.education;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.education.EducationDegreeDto;
import ru.kryuch.krtg.searcher.entity.education.EducationDegreeEntity;
import ru.kryuch.krtg.searcher.mapper.education.EducationDegreeMapper;
import ru.kryuch.krtg.searcher.repository.education.EducationDegreeRepository;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Service
@Transactional(readOnly = true)
public class EducationDegreeService extends AbstractService<Long, EducationDegreeEntity, EducationDegreeDto, EducationDegreeMapper, EducationDegreeRepository> {

    public EducationDegreeService(final EducationDegreeRepository repository, final EducationDegreeMapper mapper) {
        super(repository, mapper, "EducationDegree");
    }
}
