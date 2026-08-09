package ru.kryuch.krtg.searcher.controller.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.SendMessageParam;
import ru.kryuch.krtg.searcher.dto.view.SendVacancyMessageRequest;
import ru.kryuch.krtg.searcher.dto.view.SendVacancyMessageResponse;
import ru.kryuch.krtg.searcher.integration.dto.ChatResponse;
import ru.kryuch.krtg.searcher.service.AiGatewayService;
import ru.kryuch.krtg.searcher.service.TelegramMessagingService;
import ru.kryuch.krtg.searcher.type.SendMessageStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat/vacancy/answer")
@RequiredArgsConstructor
public class VacancyAnswerController {

    private final AiGatewayService aiGatewayService;
    private final TelegramMessagingService telegramMessagingService;

    @PostMapping("/generate")
    public String generate(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        return aiGatewayService.createMessage(text);
    }

    @PostMapping("/send")
    public SendVacancyMessageResponse send(SendVacancyMessageRequest sendVacancyMessageRequest) {
        SendMessageParam sendMessageParam =
                SendMessageParam.builder()
                        .message(sendVacancyMessageRequest.getMessage())
                        .tgAccountId(sendVacancyMessageRequest.getTgAccountId())
                        .build();

        List<ChatResponse> chatResponses =
                telegramMessagingService.registerAndSend(sendMessageParam, Set.of(sendVacancyMessageRequest.getUsername()));

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
}