## Описание проекта

Проект состоит из двух частей:
- **Python-сервер** - для работы с Telegram API (Telethon)
- **Java-сервер** - веб-интерфейс (Spring Boot) и бизнес-логика

## Требования

- Java 17+
- Python 3.10+
- Maven 3.8+
- pip (менеджер пакетов Python)

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/your-repo/tg_searcher.git
cd tg_searcher
2. Запуск Python-сервера
bash
cd kr_tg_searcher
pip install -r requirements.txt
python tg.py
При первом запуске потребуется авторизация:

Будет предложено отсканировать QR-код в мобильном Telegram

Или ввести код подтверждения

Python-сервер запустится на порту 8081

Важно: Держите терминал с Python-сервером открытым. При последующих запусках авторизация не потребуется (сессия сохраняется).

3. Настройка базы данных (H2)
Проект использует встроенную H2 Database. Ничего устанавливать не нужно.

При первом запуске Java-приложения база данных создастся автоматически в папке ./data.

Для просмотра содержимого БД:

Запустите приложение

Откройте в браузере: http://localhost:8080/h2-console

Параметры подключения:

JDBC URL: jdbc:h2:file:./data/chats_db

User Name: sa

Password: (оставьте пустым)

4. Запуск Java-приложения
bash
cd ..
mvn clean install
mvn spring-boot:run
Java-приложение запустится на порту 8080

5. Проверка работы
Откройте в браузере: http://localhost:8080

Конфигурация
Настройка подключения к Python-серверу
В файле src/main/resources/application.yml:

yaml
telegram:
  python:
    host: localhost
    port: 8081
Настройка базы данных
По умолчанию используется H2. Для переключения на PostgreSQL измените application.yml:

yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/telegram_db
    username: postgres
    password: your_password
    driver-class-name: org.postgresql.Driver
Структура проекта
text
tg_searcher/
├── kr_tg_searcher/          # Python-сервер
│   ├── tg.py                # Основной файл
│   ├── tg_messages.py       # Работа с сообщениями
│   ├── tg_search.py         # Поиск чатов
│   ├── tg_send.py           # Отправка сообщений
│   └── requirements.txt     # Зависимости Python
└── src/                     # Java-приложение
    ├── main/
    │   ├── java/            # Исходный код
    │   └── resources/       # Конфигурация и шаблоны
    └── pom.xml              # Maven конфигурация
Возможные проблемы
Python-сервер не запускается
bash
# Проверьте установку зависимостей
pip install -r requirements.txt

# Удалите старую сессию и попробуйте снова
rm -f session_web.session
python tg.py
Java не подключается к Python
Убедитесь, что Python-сервер запущен:

bash
curl http://localhost:8081/api/chats
Проверьте порт в application.yml

Проверьте, что порт 8081 не занят:

bash
sudo lsof -i:8081
Ошибка при создании таблиц в БД
Удалите папку ./data и перезапустите приложение:

bash
rm -rf ./data
mvn spring-boot:run
Остановка приложений
Python-сервер: Ctrl + C в терминале

Java-приложение: Ctrl + C в терминале

