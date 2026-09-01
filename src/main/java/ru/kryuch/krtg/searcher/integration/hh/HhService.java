package ru.kryuch.krtg.searcher.integration.hh;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerOptionEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionOptionEntity;
import ru.kryuch.krtg.searcher.integration.hh.dto.HhSettingsDto;
import ru.kryuch.krtg.searcher.integration.hh.dto.QuestionsRequest;
import ru.kryuch.krtg.searcher.integration.hh.dto.QuestionsRequestItem;
import ru.kryuch.krtg.searcher.integration.hh.dto.QuestionsResponse;
import ru.kryuch.krtg.searcher.integration.hh.dto.QuestionsResponseItem;
import ru.kryuch.krtg.searcher.integration.hh.dto.VacancyResponseRequest;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyQuestionAnswerRepository;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyQuestionRepository;
import ru.kryuch.krtg.searcher.service.SettingService;
import ru.kryuch.krtg.searcher.util.QuestionTextNormalizer;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HhService {

    private final SettingService settingService;
    private final VacancyQuestionRepository questionRepository;
    private final VacancyQuestionAnswerRepository answerRepository;

    public HhSettingsDto getSettings() {
        return new HhSettingsDto(settingService.getByCode("hh_pause").getIntValue(), settingService.getByCode("hh_letter").getStringValue());
    }

    public Long vacancyResponse(VacancyResponseRequest vacancyResponseRequest) {
        return hhService.getQuestionsAnswers(questionsRequest);
    }

    @Transactional
    public QuestionsResponse getQuestionsAnswers(QuestionsRequest questionsRequest) {
        List<QuestionsRequestItem> items = questionsRequest.getItems();

        Map<String, VacancyQuestionEntity> byNormalizedText = new HashMap<>();
        for (VacancyQuestionEntity question : questionRepository.findAllWithOptions()) {
            byNormalizedText.put(QuestionTextNormalizer.normalize(question.getText()), question);
        }

        List<VacancyQuestionEntity> resolvedQuestions = new ArrayList<>();
        for (QuestionsRequestItem item : items) {
            String normalized = QuestionTextNormalizer.normalize(item.getText());
            VacancyQuestionEntity question = byNormalizedText.get(normalized);

            if (question == null) {
                question = createQuestion(item);
                byNormalizedText.put(normalized, question);
            }
            resolvedQuestions.add(question);
        }

        Integer userId = UserUtil.getCurrentUser().getId();
        List<Integer> questionIds = resolvedQuestions.stream()
                .map(VacancyQuestionEntity::getId)
                .distinct()
                .toList();

        Map<Integer, VacancyQuestionAnswerEntity> answersByQuestionId =
                answerRepository.findAllByUserIdAndQuestionIdIn(userId, questionIds).stream()
                        .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));

        List<QuestionsResponseItem> responseItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            QuestionsRequestItem item = items.get(i);
            VacancyQuestionEntity question = resolvedQuestions.get(i);
            VacancyQuestionAnswerEntity answer = answersByQuestionId.get(question.getId());
            responseItems.add(toResponseItem(item, question, answer));
        }

        QuestionsResponse response = new QuestionsResponse();
        response.setItems(responseItems);
        return response;
    }

    private VacancyQuestionEntity createQuestion(QuestionsRequestItem item) {
        VacancyQuestionEntity question = VacancyQuestionEntity.builder()
                .text(item.getText())
                .type(item.getType())
                .required(true)
                .build();

        if (item.getOptions() != null) {
            int order = 0;
            for (String optionText : item.getOptions()) {
                question.addOption(VacancyQuestionOptionEntity.builder()
                        .text(optionText)
                        .sortOrder(order++)
                        .build());
            }
        }

        return questionRepository.save(question);
    }

    private QuestionsResponseItem toResponseItem(QuestionsRequestItem item,
                                                 VacancyQuestionEntity question,
                                                 VacancyQuestionAnswerEntity answer) {
        QuestionsResponseItem responseItem = new QuestionsResponseItem();
        responseItem.setValue(item);
        responseItem.setOptionAnswers(List.of());

        if (answer == null) {
            return responseItem;
        }

        switch (question.getType()) {
            case TEXT -> responseItem.setAnswer(answer.getTextValue());
            case YES_NO -> responseItem.setBoolAnswer(Boolean.TRUE.equals(answer.getBoolValue()));
            case SINGLE_OPTION , MULTIPLE_OPTION -> responseItem.setOptionAnswers(
                    resolveOptionIndexes(item.getOptions(), answer.getSelectedOptions()));
        }

        return responseItem;
    }

    private List<Integer> resolveOptionIndexes(List<String> requestOptions,
                                               List<VacancyQuestionAnswerOptionEntity> selectedOptions) {
        if (requestOptions == null || selectedOptions == null) {
            return List.of();
        }

        List<Integer> indexes = new ArrayList<>();
        for (VacancyQuestionAnswerOptionEntity selected : selectedOptions) {
            int index = requestOptions.indexOf(selected.getOption().getText());
            if (index >= 0) {
                indexes.add(index);
            }
            // index == -1 значит, что выбранный вариант отсутствует среди options,
            // присланных в ЭТОМ запросе — см. пояснение ниже
        }
        return indexes;
    }
}
