package ru.kryuch.krtg.searcher.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.SendMessageParam;
import ru.kryuch.krtg.searcher.dto.view.SendVacancyMessageRequest;
import ru.kryuch.krtg.searcher.dto.view.SendVacancyMessageResponse;
import ru.kryuch.krtg.searcher.integration.tg.dto.ChatResponse;
import ru.kryuch.krtg.searcher.service.AiGatewayService;
import ru.kryuch.krtg.searcher.service.MailGatewayService;
import ru.kryuch.krtg.searcher.service.TelegramMessagingService;
import ru.kryuch.krtg.searcher.type.SendMessageStatus;
import ru.kryuch.krtg.searcher.type.VacancyOwnerType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat/vacancy/answer")
@RequiredArgsConstructor
@Slf4j
public class VacancyAnswerController {

    private final AiGatewayService aiGatewayService;
    private final TelegramMessagingService telegramMessagingService;
    private final MailGatewayService mailGatewayService;

    @PostMapping("/generate")
    public String generate(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        return aiGatewayService.createMessage(text);
    }

    @PostMapping("/send")
    public SendVacancyMessageResponse send(@RequestBody SendVacancyMessageRequest sendVacancyMessageRequest) {
        switch (sendVacancyMessageRequest.getOwner().getType()) {
            case TG -> {
                return sendTg(sendVacancyMessageRequest);
            }
            case EMAIL -> {
                return sendEmail(sendVacancyMessageRequest);
            }
        }
        return new SendVacancyMessageResponse();
    }

     private SendVacancyMessageResponse sendTg(SendVacancyMessageRequest sendVacancyMessageRequest) {
        SendMessageParam sendMessageParam =
                SendMessageParam.builder()
                        .message(sendVacancyMessageRequest.getMessage())
                        .tgAccountId(sendVacancyMessageRequest.getTgAccountId())
                        .build();

        List<ChatResponse> chatResponses =
                telegramMessagingService.registerAndSend(sendMessageParam, Set.of(sendVacancyMessageRequest.getOwner().getValue()));

        SendVacancyMessageResponse sendVacancyMessageResponse = new SendVacancyMessageResponse();

        Set <String> skipUsernames =
                chatResponses.stream().filter(item -> SendMessageStatus.SKIP.equals(item.getStatus()))
                        .map(ChatResponse::getUsername)
                        .collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(skipUsernames)) {
            sendVacancyMessageResponse.setError(
                    "Пропущены: " + skipUsernames.stream().collect(Collectors.joining(", "))
            );
        }

        return sendVacancyMessageResponse;
    }

    private SendVacancyMessageResponse sendEmail(SendVacancyMessageRequest sendVacancyMessageRequest) {
        SendVacancyMessageResponse sendVacancyMessageResponse = new SendVacancyMessageResponse();
        String email = sendVacancyMessageRequest.getOwner().getValue();

        try {
            mailGatewayService.send(sendVacancyMessageRequest.getOwner().getValue(), sendVacancyMessageRequest.getMessage());
            sendVacancyMessageResponse.setSuccess(String.format("Письмо было отправлено на {}", email));
        }
        catch (Exception ex) {
            sendVacancyMessageResponse.setError(String.format("Письмо не было отправлено на {}. Ошибка {}", email, ex.getMessage()));
            log.error(String.valueOf(ex));
        }

        return sendVacancyMessageResponse;
    }
}