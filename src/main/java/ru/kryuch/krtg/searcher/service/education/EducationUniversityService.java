package ru.kryuch.krtg.searcher.service.education;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.dto.education.EducationUniversityDto;
import ru.kryuch.krtg.searcher.entity.education.EducationUniversityEntity;
import ru.kryuch.krtg.searcher.mapper.education.EducationUniversityMapper;
import ru.kryuch.krtg.searcher.repository.education.EducationUniversityRepository;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Service
@Transactional(readOnly = true)
public class EducationUniversityService extends AbstractService <Long, EducationUniversityEntity, EducationUniversityDto, EducationUniversityMapper, EducationUniversityRepository> {

    public EducationUniversityService(final EducationUniversityRepository repository, final EducationUniversityMapper mapper) {
        super(repository, mapper, "University");
    }
}
