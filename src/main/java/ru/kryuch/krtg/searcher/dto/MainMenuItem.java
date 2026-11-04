package ru.kryuch.krtg.searcher.dto;

import java.util.List;

public record MainMenuItem(String name, String path, List<SubMenuItem> subMenuItems) {}
