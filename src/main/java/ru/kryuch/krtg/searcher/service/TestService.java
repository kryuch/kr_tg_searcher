package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.helper.AuthHelper;
import ru.kryuch.krtg.searcher.integration.deepseek.DeepseekClient;
import ru.kryuch.krtg.searcher.service.deepseek.GigaRecruiterService;
import ru.kryuch.krtg.searcher.service.setting.SettingAccessService;
import ru.kryuch.krtg.searcher.type.SettingCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private static final long CRON_DELAY = 20_000;

    private final GigaRecruiterService gigaRecruiterService;
    private final AuthHelper authHelper;
    private final SettingAccessService settingAccessService;

    @Scheduled(fixedDelay = CRON_DELAY)
    public void schedule() {
        System.out.println("cron");
        settingAccessService.findAllCronEnabled(SettingCode.GIGARECRUTER_ENABLE).forEach((userId, value) -> {
            System.out.println(userId);
            authHelper.setAuth(userId);
            try {
                gigaRecruiterService.communicate();
            }
            catch (Exception ex) {
                log.error(String.valueOf(ex));
            }
        });
                /*

try {
    String task = "Раскажи чем отличаются языки Java и C#. Пиши на русском";
    String s = deepseekClient.execute(task);

    System.out.println(s);
}
catch (Exception ex) {
    System.out.println(ex);
}*/
    }
}
