package ru.kryuch.krtg.searcher.service;

import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.MessagesHistory;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.dto.VacancyInfo;

import java.util.List;

public interface ChatService {

    Boolean synchr();

    List<ChatInfo> all();

    List <ChatInfo> createNewContacts(String text);

    List<ChatInfo> search(SearchParams searchParams);

    MessagesHistory messages(Long chatId, Integer limit);

    List<VacancyInfo> vacancies(MessagesHistory messagesHistory);

    Boolean sendToVacancyOwners(MessagesHistory messagesHistory, String term, String text);

    Boolean update(Long chatId, String name, Integer status);
}
