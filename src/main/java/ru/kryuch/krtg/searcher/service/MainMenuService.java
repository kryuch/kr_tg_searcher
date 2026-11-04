package ru.kryuch.krtg.searcher.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ru.kryuch.krtg.searcher.dto.MainMenuItem;
import ru.kryuch.krtg.searcher.dto.SubMenuItem;

@Service
public class MainMenuService {
    private final List<MainMenuItem> items;

    public MainMenuService() {
        items = List.of(
            new MainMenuItem("Чаты", "/chat/", new ArrayList<>()),
            new MainMenuItem("Загрузка", "/upload/", new ArrayList<>()),
            new MainMenuItem("Синхронизация", "/synchr/", new ArrayList<>()),
            new MainMenuItem("Вопросы", "/vacancy/questions/answers/", new ArrayList<>()),
            new MainMenuItem("ТГ", "/tg/list", new ArrayList<>()),
            new MainMenuItem("Игнорирование", "/ignore/list", new ArrayList<>()),
            new MainMenuItem("Файлы", "/user/files/", new ArrayList<>()),
            new MainMenuItem("Настройки", "/settings/", new ArrayList<>()),
            new MainMenuItem("Сотрудники", "/employees/", List.of(
                new SubMenuItem("Сотрудники", "/employees/"),
                new SubMenuItem("Направления разработки", "/employees/directions/")
            ))
        );
    }

    public List<MainMenuItem> getMenuItems() {
        return items;
    }
}
