import asyncio
import logging
import threading
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
from telethon.tl.types import User, Chat, Channel, DialogFilter
from telethon.tl.functions.messages import GetDialogFiltersRequest, UpdateDialogFilterRequest

logger = logging.getLogger(__name__)

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
#  ХРАНИЛИЩЕ ДЛЯ PHONE_CODE_HASH
# ============================================================

# {phone: {'phone_code_hash': str, 'api_id': int, 'api_hash': str}}
pending_verifications = {}


# ============================================================
#  ФУНКЦИИ ДЛЯ РАБОТЫ С СЕССИЯМИ И КЛИЕНТАМИ
# ============================================================

def get_session_name(account_id):
    """Возвращает уникальное имя сессии для каждого потока."""
    thread_name = threading.current_thread().name.replace('-', '_').replace(' ', '_')
    return f'session_{account_id}_{thread_name}'


async def get_client_and_connect(account_id, account_configs):
    """Создаёт клиента, подключает и авторизует."""
    if account_id not in account_configs:
        raise ValueError(f'Аккаунт {account_id} не инициализирован')

    config = account_configs[account_id]
    session_name = get_session_name(account_id)

    print(f"🔄 Создание клиента с сессией {session_name} для потока {threading.current_thread().name}")

    client = TelegramClient(
        session_name,
        config['api_id'],
        config['api_hash'],
        device_model=DEVICE_PARAMS["device_model"],
        system_version=DEVICE_PARAMS["system_version"],
        app_version=DEVICE_PARAMS["app_version"],
        lang_code=DEVICE_PARAMS["lang_code"],
        system_lang_code=DEVICE_PARAMS["system_lang_code"]
    )

    await client.connect()
    if not await client.is_user_authorized():
        await client.start(config['phone'])
        print(f"✅ Аккаунт {account_id} авторизован в сессии {session_name}")

    return client


async def execute_telegram_action(account_id, account_configs, action_func, *args, **kwargs):
    """Универсальный исполнитель: создаёт клиента, выполняет действие, отключает."""
    client = await get_client_and_connect(account_id, account_configs)
    try:
        return await action_func(client, *args, **kwargs)
    finally:
        await client.disconnect()
        print(f"🔌 Клиент {account_id} отключён")


# ============================================================
#  ФУНКЦИИ ДЛЯ АВТОРИЗАЦИИ (С СОХРАНЕНИЕМ PHONE_CODE_HASH)
# ============================================================

async def request_code_internal(phone, api_id, api_hash):
    """
    Запрашивает код подтверждения у Telegram.
    Сохраняет phone_code_hash для дальнейшей верификации.
    """
    client = TelegramClient(
        f'temp_session_{phone}',
        int(api_id),
        api_hash,
        device_model=DEVICE_PARAMS["device_model"],
        system_version=DEVICE_PARAMS["system_version"],
        app_version=DEVICE_PARAMS["app_version"],
        lang_code=DEVICE_PARAMS["lang_code"],
        system_lang_code=DEVICE_PARAMS["system_lang_code"]
    )

    try:
        await client.connect()
        result = await client.send_code_request(phone)
        print(f"✅ Код отправлен на {phone}")

        # Сохраняем phone_code_hash
        pending_verifications[phone] = {
            'phone_code_hash': result.phone_code_hash,
            'api_id': int(api_id),
            'api_hash': api_hash
        }

        return {'status': 'code_sent', 'phone': phone}
    finally:
        await client.disconnect()
        print(f"🔌 Временный клиент для {phone} отключён")


async def verify_code_with_new_client(phone, code, password=None):
    """
    Создаёт нового клиента и проверяет код используя сохранённый phone_code_hash.
    """
    if phone not in pending_verifications:
        raise ValueError(f'Не найден phone_code_hash для {phone}. Сначала запросите код.')

    data = pending_verifications[phone]
    phone_code_hash = data['phone_code_hash']
    api_id = data['api_id']
    api_hash = data['api_hash']

    client = TelegramClient(
        f'session_{phone}',
        int(api_id),
        api_hash,
        device_model=DEVICE_PARAMS["device_model"],
        system_version=DEVICE_PARAMS["system_version"],
        app_version=DEVICE_PARAMS["app_version"],
        lang_code=DEVICE_PARAMS["lang_code"],
        system_lang_code=DEVICE_PARAMS["system_lang_code"]
    )

    try:
        await client.connect()

        try:
            await client.sign_in(phone=phone, code=code, phone_code_hash=phone_code_hash)
            print(f"✅ Аккаунт {phone} успешно авторизован")

            del pending_verifications[phone]
            return {'status': 'success', 'phone': phone}

        except SessionPasswordNeededError:
            if password:
                await client.sign_in(password=password)
                print(f"✅ Аккаунт {phone} авторизован с 2FA")
                del pending_verifications[phone]
                return {'status': 'success', 'phone': phone}
            else:
                return {'status': 'password_required'}

        except PhoneCodeInvalidError:
            raise ValueError('Неверный код подтверждения')

        except PhoneCodeExpiredError:
            raise ValueError('Код подтверждения истёк')

    finally:
        await client.disconnect()
        print(f"🔌 Клиент для {phone} отключён")


# ============================================================
#  ОСТАЛЬНЫЕ ФУНКЦИИ
# ============================================================

def _peer_id(peer):
    """Возвращает ID чата из InputPeer*."""
    return (
        getattr(peer, "user_id", None)
        or getattr(peer, "channel_id", None)
        or getattr(peer, "chat_id", None)
    )


def _peers_equal(p1, p2):
    """Сравнивает два Peer объекта"""
    return _peer_id(p1) == _peer_id(p2)


def clone_filter(filter_obj, include_peers):
    return DialogFilter(
        id=filter_obj.id,
        title=filter_obj.title,
        pinned_peers=filter_obj.pinned_peers,
        include_peers=include_peers,
        exclude_peers=filter_obj.exclude_peers,
        contacts=filter_obj.contacts,
        non_contacts=filter_obj.non_contacts,
        groups=filter_obj.groups,
        broadcasts=filter_obj.broadcasts,
        bots=filter_obj.bots,
        exclude_muted=filter_obj.exclude_muted,
        exclude_read=filter_obj.exclude_read,
        exclude_archived=filter_obj.exclude_archived,
        emoticon=filter_obj.emoticon,
        color=getattr(filter_obj, "color", None),
        title_noanimate=getattr(filter_obj, "title_noanimate", False),
    )


async def update_chat_folders(client, folder_id, chat_ids, add_to_folder):
    """Добавляет или удаляет список чатов из пользовательской папки Telegram."""
    try:
        filters = await client(GetDialogFiltersRequest())
        filters = filters.filters if hasattr(filters, "filters") else list(filters)

        folder = next(
            (f for f in filters if getattr(f, "id", None) == folder_id),
            None
        )

        if folder is None:
            return {
                "success": False,
                "error": f"Folder {folder_id} not found"
            }

        include_peers = list(folder.include_peers)
        current_ids = {_peer_id(peer) for peer in include_peers}
        results = []

        for chat_id in chat_ids:
            try:
                peer = await client.get_input_entity(chat_id)

                if add_to_folder:
                    if chat_id not in current_ids:
                        include_peers.append(peer)
                        current_ids.add(chat_id)
                else:
                    include_peers = [
                        p for p in include_peers
                        if _peer_id(p) != chat_id
                    ]
                    current_ids.discard(chat_id)

                results.append({
                    "chat_id": chat_id,
                    "status": "success"
                })

            except FloodWaitError as e:
                await asyncio.sleep(e.seconds)
                results.append({
                    "chat_id": chat_id,
                    "status": "error",
                    "error": f"FloodWait {e.seconds}"
                })

            except Exception as e:
                logger.exception("Cannot process chat %s", chat_id)
                results.append({
                    "chat_id": chat_id,
                    "status": "error",
                    "error": str(e)
                })

        await client(
            UpdateDialogFilterRequest(
                id=folder.id,
                filter=clone_filter(folder, include_peers)
            )
        )

        return {
            "success": True,
            "folder_id": folder_id,
            "operation": "add" if add_to_folder else "remove",
            "results": results
        }

    except Exception:
        logger.exception("Cannot update folder %s", folder_id)
        return {
            "success": False,
            "error": "UPDATE_FAILED"
        }


async def get_all_folders(client):
    """Возвращает список пользовательских папок Telegram."""
    try:
        result = await client(GetDialogFiltersRequest())
        filters = result.filters if hasattr(result, "filters") else list(result)

        folders = []
        for dialog_filter in filters:
            folder_id = getattr(dialog_filter, "id", None)
            if folder_id is None:
                continue

            title = getattr(dialog_filter, "title", None)
            if hasattr(title, "text"):
                title = title.text

            chat_ids = [
                peer_id
                for peer in getattr(dialog_filter, "include_peers", [])
                if (peer_id := _peer_id(peer)) is not None
            ]

            folders.append({
                "id": folder_id,
                "title": title,
                "chatIds": chat_ids
            })

        return folders

    except Exception:
        logger.exception("Ошибка получения списка папок")
        return []


async def get_chat_folders(client, chat_id):
    """Получает список папок, в которых находится чат."""
    folders = []
    try:
        dialogs = await client.get_dialogs()

        folder_ids = set()
        for dialog in dialogs:
            if dialog.id == chat_id and dialog.folder_id is not None:
                folder_ids.add(dialog.folder_id)

        if folder_ids:
            all_folders = await get_all_folders(client)
            for folder in all_folders:
                if folder['id'] in folder_ids:
                    folders.append(folder)
    except Exception as e:
        print(f"❌ Ошибка получения папок для чата {chat_id}: {e}")

    return folders


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