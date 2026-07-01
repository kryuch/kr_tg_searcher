package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.service.ChatService;

@RestController
@RequestMapping("/chat/status")
@RequiredArgsConstructor
public class ChatStatusController {

    private final ChatService chatServiceImpl;

    @PostMapping("/update")
    public Boolean update(@Param("chatId") Long chatId, @Param("username") String username, @Param("name") String name, Integer status) {
        return chatServiceImpl.update(chatId, username, name, status);
    }

//  //  public Boolean synchr() {
  //      return chatServiceImpl.synchr();
   // }
}