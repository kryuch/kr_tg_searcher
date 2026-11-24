package ru.kryuch.krtg.searcher.service.employee;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDto;

@Component
@RequiredArgsConstructor
public class EmployeeResumeJsonParser {
    private final ObjectMapper objectMapper;

    public ResumeDto parse(String json) {
        if (json == null || json.isBlank()) {
            throw new IllegalStateException("Получен пустой ответ");
        }

        try {
            ResumeDto resume = objectMapper.readValue(json, ResumeDto.class);

            if (resume == null || resume.getPerson() == null) {
                throw new IllegalStateException("В JSON резюме отсутствует объект person");
            }

            return resume;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось разобрать JSON резюме", e);
        }
    }
}