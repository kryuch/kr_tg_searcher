import asyncio
import logging
from telethon.errors import FloodWaitError, ChatAdminRequiredError, ChannelPrivateError, UserIdInvalidError
from telethon.tl.types import User, Chat, Channel
from telethon.tl.functions.messages import GetDialogFiltersRequest

logger = logging.getLogger(__name__)

# Ограничиваем количество одновременных запросов к Telegram
SEMAPHORE = asyncio.Semaphore(5)

async def get_single_chat_info(client, chat_id, semaphore=None):
    """
    Получает информацию об одном чате.
    """
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

            logger.info(f"Получена информация о чате {entity_id}: {name}")

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
        logger.warning(f"Чат {chat_id} не найден")
        return {
            'id': chat_id,
            'username': None,
            'name': None,
            'success': False,
            'error': 'NOT_FOUND'
        }

    except (ChatAdminRequiredError, ChannelPrivateError) as e:
        logger.warning(f"Нет доступа к чату {chat_id}: {e}")
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
    """
    Получает информацию о чатах по их ID параллельно.
    """
    if not chat_ids:
        return []

    semaphore = asyncio.Semaphore(max_concurrent)

    tasks = [
        get_single_chat_info(client, chat_id, semaphore)
        for chat_id in chat_ids
    ]

    results = await asyncio.gather(*tasks)
    return results

async def get_all_folders(client):
    """
    Получает список всех папок пользователя.

    Возвращает:
    - список словарей с полями: id, title
    """
    folders = []
    try:
        result = await client(GetDialogFiltersRequest())

        # Получаем список фильтров/папок
        filters = result.filters if hasattr(result, 'filters') else list(result)

        for f in filters:
            # Пропускаем пустую/дефолтную папку
            if getattr(f, 'id', None) is None:
                continue

            # Получаем название
            title = getattr(f, 'title', None)
            if title and hasattr(title, 'text'):
                title = title.text

            folder_id = getattr(f, 'id', None) or getattr(f, 'folder_id', None)

            if folder_id is not None and title:
                folders.append({
                    'id': folder_id,
                    'title': title
                })
    except Exception as e:
        print(f"❌ Ошибка получения папок: {e}")

    return folders

async def get_chat_folders(client, chat_id):
    """
    Получает список папок, в которых находится чат.
    """
    folders = []
    try:
        # Получаем все диалоги
        dialogs = await client.get_dialogs()

        # Собираем все folder_id, где есть чат
        folder_ids = set()
        for dialog in dialogs:
            if dialog.id == chat_id and dialog.folder_id is not None:
                folder_ids.add(dialog.folder_id)
                print(f"🔍 Найден folder_id: {dialog.folder_id} для чата {chat_id}")

        # Получаем названия папок
        if folder_ids:
            all_folders = await get_all_folders(client)
            for folder in all_folders:
                if folder['id'] in folder_ids:
                    folders.append(folder)
                    print(f"✅ Папка: {folder['title']} (ID: {folder['id']}")
    except Exception as e:
        print(f"❌ Ошибка получения папок для чата {chat_id}: {e}")

    return folders