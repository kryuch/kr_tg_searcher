package ru.kryuch.krtg.searcher.service.education;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.kryuch.krtg.searcher.dto.education.EducationSpecialtyDto;
import ru.kryuch.krtg.searcher.entity.education.EducationSpecialtyEntity;
import ru.kryuch.krtg.searcher.mapper.education.EducationSpecialtyMapper;
import ru.kryuch.krtg.searcher.repository.education.EducationSpecialtyRepository;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Service
@Transactional(readOnly = true)
public class EducationSpecialtyService extends AbstractService <Long, EducationSpecialtyEntity, EducationSpecialtyDto, EducationSpecialtyMapper, EducationSpecialtyRepository> {


    public EducationSpecialtyService(final EducationSpecialtyRepository repository, final EducationSpecialtyMapper mapper) {
        super(repository, mapper, "Speciality");
    }
}
