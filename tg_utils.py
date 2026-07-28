import asyncio
import logging
import threading
from pathlib import Path
from telethon import TelegramClient
from telethon.errors import (
    FloodWaitError,
    ChatAdminRequiredError,
    ChannelPrivateError,
    UserIdInvalidError,
    SessionPasswordNeededError,
    PhoneCodeInvalidError,
    PhoneCodeExpiredError
)
from telethon.tl.types import User, Chat, Channel

# Импортируем функции работы с папками из отдельного модуля
from tg_folders import (
    update_chat_folders,
    get_all_folders,
    get_chat_folders,
    process_update_folders
)

SESSION_DIR = Path("var/session")
SESSION_DIR.mkdir(parents=True, exist_ok=True)

logger = logging.getLogger(__name__)


def build_session_path(session_name: str) -> str:
    """
    Возвращает полный путь к session-файлу.
    """
    return str(SESSION_DIR / session_name)


# Ограничиваем количество одновременных запросов к Telegram
SEMAPHORE = asyncio.Semaphore(5)

# Параметры устройства для Telethon
DEVICE_PARAMS = {
    "device_model": "Samsung S23 Ultra",
    "system_version": "Android 13",
    "app_version": "9.6.1",
    "lang_code": "en",
    "system_lang_code": "en-US"
}

# ============================================================
#  ПУЛ TELEGRAM-КЛИЕНТОВ
# ============================================================

telegram_clients = {}
telegram_locks = {}


async def get_client(account_id: str, account_configs: dict):
    """
    Возвращает единственный TelegramClient для аккаунта.
    Если клиента нет — создает и подключает.
    """
    account_id = str(account_id)

    if account_id in telegram_clients:
        client = telegram_clients[account_id]
        if client.is_connected():
            return client
        await client.connect()
        return client

    config = account_configs[account_id]
    session_path = build_session_path(f"account_{account_id}")

    client = TelegramClient(
        session_path,
        config["api_id"],
        config["api_hash"],
        device_model=DEVICE_PARAMS["device_model"],
        system_version=DEVICE_PARAMS["system_version"],
        app_version=DEVICE_PARAMS["app_version"],
        lang_code=DEVICE_PARAMS["lang_code"],
        system_lang_code=DEVICE_PARAMS["system_lang_code"]
    )

    await client.connect()

    if not await client.is_user_authorized():
        raise RuntimeError(f"Аккаунт {account_id} не авторизован")

    telegram_clients[account_id] = client
    telegram_locks[account_id] = asyncio.Lock()

    logger.info("Telegram client %s created", account_id)

    return client


async def execute_telegram_action(
        account_id,
        account_configs,
        action,
        *args,
        **kwargs
):
    """
    Выполняет действие через единственный клиент аккаунта.
    Для каждого аккаунта одновременно выполняется только одна операция.
    """
    account_id = str(account_id)

    client = await get_client(account_id, account_configs)

    lock = telegram_locks[account_id]

    async with lock:
        if client.is_connected():
            try:
                await client.get_me()
            except Exception:
                await client.connect()
        else:
            await client.connect()

        return await action(client, *args, **kwargs)


# ============================================================
#  ФУНКЦИИ ДЛЯ АВТОРИЗАЦИИ
# ============================================================

async def request_code_internal(
        phone: str,
        api_id: int,
        api_hash: str,
        session_name: str
):
    """
    Создает временный клиент и отправляет код авторизации.
    """
    session_path = build_session_path(session_name)

    logger.info(f"📱 Запрос кода для телефона: {phone}")
    logger.info(f"🔑 Используется сессия: {session_path}")
    logger.info(f"🆔 API ID: {api_id}")

    client = TelegramClient(
        session_path,
        api_id,
        api_hash,
        device_model="Samsung S23 Ultra",
        system_version="Android 13",
        app_version="9.6.1",
        lang_code="en",
        system_lang_code="en-US"
    )

    try:
        logger.debug("🔗 Подключение к Telegram...")
        await client.connect()
        logger.debug("✅ Подключение установлено")

        is_authorized = await client.is_user_authorized()
        logger.debug(f"🔐 Статус авторизации: {is_authorized}")

        if is_authorized:
            logger.info(f"✅ Аккаунт {phone} уже авторизован")
            response = {
                "success": True,
                "authorised": True,
                "error": None,
                "wait_seconds": None
            }
            logger.info(f"📤 Python ответ: {response}")
            return response

        try:
            sent = await client.send_code_request(phone)
            logger.info(f"✅ Код успешно отправлен на {phone}")
            logger.debug(f"📋 phone_code_hash: {sent.phone_code_hash}")

            return {
                "success": True,
                "status": "code_sent",
                "authorised": False,
                "phone_code_hash": sent.phone_code_hash
            }

        except FloodWaitError as e:
            logger.warning(f"⏳ FloodWait: необходимо подождать {e.seconds} секунд для {phone}")
            return {
                "status": "flood_wait",
                "authorised": False,
                "wait_seconds": e.seconds
            }

        except Exception as e:
            logger.error(f"❌ Ошибка при отправке кода для {phone}: {type(e).__name__}: {e}")
            raise

    except Exception as e:
        logger.error(f"❌ Критическая ошибка в request_code_internal для {phone}: {type(e).__name__}: {e}")
        raise

    finally:
        logger.debug(f"🔌 Отключение клиента для {phone}...")
        await client.disconnect()
        logger.debug(f"✅ Клиент для {phone} отключён")


async def verify_code_with_new_client(
    account_id,
    phone,
    code,
    phone_code_hash,
    password,
    api_id,
    api_hash,
    session_name
):
    """
    Завершает авторизацию Telegram-аккаунта.
    Добавлены таймауты на подключение и sign_in.
    """
    import asyncio

    print(f"🔵 verify_code_with_new_client: НАЧАЛО для {account_id}")

    session_path = build_session_path(session_name)
    print(f"🔵 verify_code_with_new_client: session_path={session_path}")

    client = TelegramClient(
        session_path,
        api_id,
        api_hash,
        device_model="Samsung S23 Ultra",
        system_version="Android 13",
        app_version="9.6.1",
        lang_code="en",
        system_lang_code="en-US"
    )

    try:
        print(f"🔵 verify_code_with_new_client: подключение к {account_id}...")
        await asyncio.wait_for(client.connect(), timeout=10.0)
        print(f"🔵 verify_code_with_new_client: подключено к {account_id}")

        old = telegram_clients.pop(account_id, None)
        if old:
            try:
                await old.disconnect()
            except:
                pass

        telegram_locks.pop(account_id, None)

        if await client.is_user_authorized():
            return {
                "status": "success",
                "message": "Аккаунт уже авторизован"
            }

        print(f"🔵 verify_code_with_new_client: sign_in для {account_id}...")
        await asyncio.wait_for(
            client.sign_in(
                phone=phone,
                code=code,
                phone_code_hash=phone_code_hash
            ),
            timeout=15.0
        )

        return {
            "status": "success"
        }

    except asyncio.TimeoutError:
        print(f"⏰ Таймаут в verify_code_with_new_client для {account_id}")
        return {
            "status": "error",
            "message": "Превышено время ожидания ответа от Telegram"
        }

    except SessionPasswordNeededError:
        if not password:
            return {
                "status": "password_required"
            }
        await client.sign_in(password=password)
        return {"status": "success"}

    except PhoneCodeInvalidError:
        return {
            "status": "invalid_code",
            "message": "Неверный код"
        }

    except PhoneCodeExpiredError:
        return {
            "status": "code_expired",
            "message": "Код истек"
        }

    except Exception as e:
        logger.exception("Ошибка авторизации")
        return {
            "status": "error",
            "message": str(e)
        }

    finally:
        await client.disconnect()
        print(f"🔌 Клиент для {account_id} отключён")


# ============================================================
#  ФУНКЦИИ ДЛЯ РАБОТЫ С ЧАТАМИ
# ============================================================

async def get_single_chat_info(client, chat_id, semaphore=None):
    """Получает информацию об одном чате."""
    if semaphore is None:
        semaphore = SEMAPHORE

    try:
        try:
            if isinstance(chat_id, str):
                chat_id = int(chat_id)
        except ValueError:
            return {
                'id': chat_id,
                'username': None,
                'name': None,
                'success': False,
                'error': 'INVALID_CHAT_ID'
            }

        async with semaphore:
            entity = await client.get_entity(chat_id)

            if isinstance(entity, (Chat, Channel)):
                name = entity.title
            elif isinstance(entity, User):
                name = entity.first_name or ''
                if entity.last_name:
                    name += " " + entity.last_name
                name = name.strip() or None
            else:
                name = None

            username = getattr(entity, 'username', None)
            entity_id = entity.id

            return {
                'id': entity_id,
                'username': username,
                'name': name,
                'success': True,
                'error': None
            }

    except FloodWaitError as e:
        wait_time = e.seconds
        logger.warning(f"FloodWait для чата {chat_id}: {wait_time} сек")
        await asyncio.sleep(wait_time)
        return await get_single_chat_info(client, chat_id, semaphore)

    except (ValueError, UserIdInvalidError):
        return {
            'id': chat_id,
            'username': None,
            'name': None,
            'success': False,
            'error': 'NOT_FOUND'
        }

    except (ChatAdminRequiredError, ChannelPrivateError) as e:
        return {
            'id': chat_id,
            'username': None,
            'name': None,
            'success': False,
            'error': 'ACCESS_DENIED'
        }

    except Exception as e:
        logger.error(f"Ошибка при получении чата {chat_id}: {e}")
        return {
            'id': chat_id,
            'username': None,
            'name': None,
            'success': False,
            'error': 'UNKNOWN_ERROR'
        }


async def get_chats_info(client, chat_ids, max_concurrent=5):
    """Получает информацию о чатах по их ID параллельно."""
    if not chat_ids:
        return []

    semaphore = asyncio.Semaphore(max_concurrent)

    tasks = [
        get_single_chat_info(client, chat_id, semaphore)
        for chat_id in chat_ids
    ]

    results = await asyncio.gather(*tasks)
    return results


# ============================================================
#  ЗАВЕРШЕНИЕ КЛИЕНТОВ
# ============================================================

async def shutdown_clients():
    """
    Корректно закрывает все TelegramClient.
    """
    for client in telegram_clients.values():
        try:
            if client.is_connected():
                await client.disconnect()
        except Exception:
            logger.exception("Cannot disconnect client")

    telegram_clients.clear()
    telegram_locks.clear()