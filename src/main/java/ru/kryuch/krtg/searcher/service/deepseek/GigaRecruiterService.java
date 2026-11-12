package ru.kryuch.krtg.searcher.service.deepseek;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.Message;
import ru.kryuch.krtg.searcher.integration.deepseek.DeepseekClient;
import ru.kryuch.krtg.searcher.integration.tg.TelegrammRecruterClient;
import ru.kryuch.krtg.searcher.service.SettingService;

@Service
@RequiredArgsConstructor
public class GigaRecruiterService {

    private final TelegrammRecruterClient telegrammRecruterClient;
    private final DeepseekClient deepseekClient;
    private final SettingService settingService;

    public void communicate() throws InterruptedException {
        //   do {
        Message message = telegrammRecruterClient.getMessage();
        if (message.getOwnerFlag()) return;

        if (isHelloMessage(message.getValue())) {
            telegrammRecruterClient.sendMessage("Да");
            return;
        }

        if (isWhyMessage(message.getValue())) {
            telegrammRecruterClient.sendMessage("Мне интересны задачи, описанные в вакансии");
            return;
        }

        if (isLastMessage(message.getValue())) {
            telegrammRecruterClient.sendMessage("3");
            return;
        }

        String answer = deepseekClient.execute(prepareMessage(message.getValue()));
        telegrammRecruterClient.sendMessage(answer);
        //  }
        //   while (true);
    }

    private String prepareMessage(String question) {
        String resume = settingService.getValueByCode("resume");
        return "На основе моего резюме напиши ответ на вопрос рекрутера: \t" + question + "\". Ответ напиши на русском, без форматирования (просто текст) примерно 2 абзаца. Вот мое резюме: " + resume.translateEscapes() + ". ";
    }

    private boolean isHelloMessage(String message) {
        String pattern = "Будет удобно прямо сейчас ответить на несколько вопросов по этой позиции? Это позволит ускорить процесс вашего рассмотрения.";
        return (message.indexOf(pattern) > 0);
    }

    private boolean isWhyMessage(String message) {
        String pattern = "Чем вас заинтересовала данная вакансия?";
        return (message.indexOf(pattern) > 0);
    }

    private boolean isLastMessage(String message) {
        String pattern = "оцените мою работу!";
        return (message.indexOf(pattern) > 0);
    }

    private boolean isFinishMessage(String message) {
        String pattern = "у вас нет других вакансий, по которым можно пройти первичное интервью";
        return (message.indexOf(pattern) > 0);
    }

}
