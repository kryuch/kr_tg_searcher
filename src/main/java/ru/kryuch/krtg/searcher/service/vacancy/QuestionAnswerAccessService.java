package ru.kryuch.krtg.searcher.service.vacancy;

import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.QuestionDto;
import ru.kryuch.krtg.searcher.dto.view.QuestionAnswerDto;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerEntity;
import ru.kryuch.krtg.searcher.mapper.vacancy.VacancyQuestionAnswerMapper;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyQuestionAnswerRepository;
import ru.kryuch.krtg.searcher.service.AbstractAccessService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionAnswerAccessService { /*extends AbstractAccessService<Integer, VacancyQuestionAnswerEntity, QuestionAnswerDto, VacancyQuestionAnswerMapper, VacancyQuestionAnswerRepository> {


    public QuestionAnswerAccessService(VacancyQuestionAnswerRepository repository, VacancyQuestionAnswerMapper mapper) {
        super(repository, mapper, "ответ на вопрос");
    }*/

    public void rebuild(List <QuestionDto> questions) {
/*
        Set<Integer> newQuestionIds =
                ((VacancyQuestionAnswerRepository) repository).findNotExistingQuestionIds(
                        questions.stream().map(QuestionDto::getId).collect(Collectors.toSet()),
                        getCurrentUserId()
                );

        Set<VacancyQuestionAnswerEntity> questionAnswerEntities =
                newQuestionIds.stream().map(item -> {
                    VacancyQuestionAnswerEntity questionAnswerEntity = new VacancyQuestionAnswerEntity();
                    questionAnswerEntity.setUserId(getCurrentUserId());
                    questionAnswerEntity.setQuestionId(item);
                    return questionAnswerEntity;
                }).collect(Collectors.toSet());

        repository.saveAll(questionAnswerEntities);*/
    }
}