package ru.kryuch.krtg.searcher.integration.hh.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HhSettingsDto {

    private final int delaySecondCount;

    private final String coverLetter;
}
