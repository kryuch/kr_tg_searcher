package ru.kryuch.krtg.searcher.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettingCode {

    // Cron
    CRON_ENABLE("cron_enable"),
    CRON_TIME("cron_time"),
    CRON_LAST_MESSAGE("cron_lastmessage"),
    CRON_NEW_MESSAGE("cron_newmessage"),
    CRON_LAST_RUN("cron_lastrun"),
    CRON_CHATS_COUNT("cron_chats_count"),

    // Folder
    FOLDER_ENABLE("folder_enable"),
    FOLDER("folder"),
    ADD_TO_FOLDER("add_to_folder"),
    TEXT_IN_VACANCY("text_in_vacancy"),

    // AI
    AI_ENABLE("ai_enable"),
    AI_MODEL("ai_model"),
    AI_API_KEY("ai_api_key"),
    AI_VACANCY_PROMPT("ai_vacancy_promt"),

    // General
    RESUME("resume"),
    TERM("term"),
    MAX_DAY("max_day"),
    IGNORE("ignore"),
    SEND_DELAY("send_delay"),
    FIRST_MESSAGE("first_message"),

    // HH (HeadHunter)
    HH_ENABLE("hh_enable"),
    HH_PAUSE("hh_pause"),
    HH_LOG("hh_log"),
    HH_LETTER("hh_letter"),

    // Gmail
    GMAIL_EMAIL("gmail_email"),
    GMAIL_REFRESH_TOKEN("gmail_refresh_token"),
    GMAIL_SUBJECT("gmail_subject"),

    // Admin
    PYTHON("python"),
    IGNORE_IF_NOT_FOUND("ignore_if_not_found");

    private final String code;

    public static SettingCode fromCode(String code) {
        for (SettingCode value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown setting code: " + code);
    }
}