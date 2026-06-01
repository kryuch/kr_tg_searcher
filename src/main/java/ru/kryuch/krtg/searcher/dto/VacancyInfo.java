package ru.kryuch.krtg.searcher.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Builder
public class VacancyInfo {

    // название вакансии
    String title = "";

    // полный текст
    String text;

    // кто разместил (телеграмм, почта или ссылка)
    List<VacancyOwnerInfo> owners;

    // дата и вреся размещения
    LocalDateTime dateTime;

    // статус (1 - удаленная, 2 - орфис, 3 - гибрид или неясно (не удалось однозначно отпарсить)
    Integer remoteStatus;

    Integer status = 0;

    public String getTg() {
        Optional <VacancyOwnerInfo> vacancyOwnerInfo =
                owners.stream().filter(item -> item.getType() == 3).findFirst();
        return (vacancyOwnerInfo.isPresent()) ? vacancyOwnerInfo.get().getValue() : null;
    }
}
