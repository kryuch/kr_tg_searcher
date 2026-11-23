package ru.kryuch.krtg.searcher.service.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDto;
import ru.kryuch.krtg.searcher.exception.BusinessException;
import ru.kryuch.krtg.searcher.integration.deepseek.DeepseekClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Lazy
public class EmployeeResumeBuilderService {
    private final DeepseekClient deepseekClient;
    private final EmployeeResumeJsonParser parser;

    private final ConcurrentMap<Integer, ResumeSession> sessions = new ConcurrentHashMap<>();

    private static String prepareMessage() {
        return "Тебе нужно превратить произвольные сведения о человеке и его навыках в JSON-документ.Ответ возвращается в формате JSON, без пояснений и блока Markdown. Корневой объект содержит четыре поля: schemaVersion - целое число, версия схемы (обычно 1); person - объект с данными человека; education - массив записей об образовании; profiles - массив профилей (направлений) с навыками и проектами.Объект person содержит: lastName, firstName, middleName - строки (фамилия, имя, отчество); birthDate - дата рождения строкой в формате ГГГГ-ММ-ДД; gender - строка, строго одно из двух значений: MALE (мужчина) или FEMALE (женщина); citizenship - строка (гражданство).Массив education состоит из объектов, каждый содержит: university - строка (вуз); specialty - строка (специальность); educationType - строка (тип образования, например высшее); startYear и endYear - целые числа (годы начала и окончания).Массив profiles состоит из объектов, каждый содержит: direction - строка (направление, например Backend); grade - строка (грейд, например Junior, Middle, Senior); skills - массив навыков; projects - массив проектов.Каждый объект навыка в skills содержит: name - строка (название навыка); category - строка, строго одно из значений: LANGUAGE (язык программирования), FRAMEWORK (фреймворк), DATABASE (база данных), TOOL (инструмент), METHODOLOGY (методология), SOFT_SKILL (soft skill), OTHER (другое); level - целое число (уровень владения).Каждый объект проекта в projects содержит: organization - строка (организация); position - строка (должность); startDate и endDate - даты строкой в формате ГГГГ-ММ-ДД; description - строка (описание); technologies - массив строк (использованные технологии).Правила заполнения: категории навыков и пол записывай только именами констант из списков выше, а не их русскими подписями; все даты только в формате ГГГГ-ММ-ДД; ничего не выдумывай - если данных нет, ставь null или опускай необязательное поле; если список пуст, оставляй пустой массив; числа записывай без кавычек, строки - в кавычках.Пример готового документа:{\"schemaVersion\":1,\"person\":{\"lastName\":\"Иванов\",\"firstName\":\"Иван\",\"middleName\":\"Иванович\",\"birthDate\":\"1995-04-12\",\"gender\":\"MALE\",\"citizenship\":\"РФ\"},\"education\":[{\"university\":\"МГУ\",\"specialty\":\"Прикладная математика\",\"educationType\":\"высшее\",\"startYear\":2013,\"endYear\":2017}],\"profiles\":[{\"direction\":\"Backend\",\"grade\":\"Middle\",\"skills\":[{\"name\":\"Java\",\"category\":\"LANGUAGE\",\"level\":4},{\"name\":\"Spring\",\"category\":\"FRAMEWORK\",\"level\":3}],\"projects\":[{\"organization\":\"ООО Ромашка\",\"position\":\"Разработчик\",\"startDate\":\"2020-01-10\",\"endDate\":\"2022-06-30\",\"description\":\"Разработка API\",\"technologies\":[\"Java\",\"Spring\",\"PostgreSQL\"]}]}]}. Как будешь готов к работе, напиши в чате \"READY\"";
    }

    private void prepareSession(Integer groupId) throws InterruptedException {
        String response = deepseekClient.execute(groupId, prepareMessage());
        if (response == null || !"READY".equalsIgnoreCase(response.strip())) {
            throw new BusinessException("DeepSeek не подтвердил готовность сессии " + groupId);
        }
    }

    public ResumeDto serializeToResumeDto(String input) {
        Integer groupId = deepseekClient.getCurrentGroupId();
        ResumeSession session = sessions.computeIfAbsent(groupId, id -> new ResumeSession());

        synchronized (session) {
            try {
                if (!session.prepared) {
                    prepareSession(groupId);
                    session.prepared = true;
                }
                return parser.parse(deepseekClient.execute(groupId, input));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException("Обработка резюме прервана", e);
            }
        }
    }

    private static class ResumeSession {
        private boolean prepared;
    }
}
