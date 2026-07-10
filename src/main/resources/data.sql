-- Вставляем только если записи ещё нет
INSERT INTO krrg_settings (id, code, setting_value)
SELECT 1, 'first_message', 'Добрый день. Скажите, пожалуйста, у вас вакансии по Java-разработке'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'first_message');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 2, 'term', 'Java'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'term');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 3, 'folder', 'HR'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'folder');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 4, 'max_day', '3'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'max_day');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 5, 'ignore', 'СВО'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'ignore');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 6, 'python', 'http://localhost:8081'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'python');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 7, 'send_delay', '10'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'send_delay');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 8, 'text_in_vacancy', 'Java'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'text_in_vacancy');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 9, 'tg_folder', 'HR'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'tg_folder');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 10, 'cron_time', '0 0 7 * * *'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'cron_time');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 11, 'cron_lastmessage', '*'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'cron_lastmessage');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 12, 'cron_newmessage', '*'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'cron_newmessage');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 13, 'cron_lastrun', ''
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'cron_lastrun');

INSERT INTO krrg_settings (id, code, setting_value)
SELECT 14, 'cron_enable', '0'
    WHERE NOT EXISTS (SELECT 1 FROM krrg_settings WHERE code = 'cron_enable');