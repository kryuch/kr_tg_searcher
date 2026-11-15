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
            new MainMenuItem("Сотрудники", "/employee/", List.of(
                new SubMenuItem("Сотрудники", "/employee/"),
                // new SubMenuItem("Профили", "/employee/profiles/"),
                new SubMenuItem("Направления разработки", "/employee/directions/"),
                new SubMenuItem("Уровни разработчика", "/employee/grades/"),
                new SubMenuItem("Навыки и технологии", "/employee/skills/"),
                new SubMenuItem("Компании, в которых работал", "/employee/organizations/"),
                new SubMenuItem("Университеты", "/employee/universities/"),
                new SubMenuItem("Специальности", "/employee/specialties/"),
                new SubMenuItem("Типы образования", "/employee/educationtypes/")
            ))
        );
    }

    public List<MainMenuItem> getMenuItems() {
        return items;
    }
}
