import asyncio
from datetime import datetime
from telethon.errors import ChatAdminRequiredError, ChannelPrivateError, UserIdInvalidError

async def search_chats(client, params):
    """
    Поиск чатов по параметрам

    params: {
        'term': str,                 # ключевое слово
        'maxFoundCount': int,        # максимальное количество чатов
        'minDiffDaysCount': int,     # минимальное количество дней без сообщений
        'botType': str,              # PERSONAL, NOT_PERSONAL, ALL
        'groupType': str,            # PERSONAL, NOT_PERSONAL, ALL
        'excludeChatIds': list,      # список ID чатов для исключения
        'messagesCount': int         # количество последних сообщений для каждого чата (0 - не добавлять)
    }
    """
    term = params.get('term', 'Java')
    max_found_count = params.get('maxFoundCount', 10)
    min_diff_days_count = params.get('minDiffDaysCount', 7)
    bot_type = params.get('botType', 'PERSONAL')
    group_type = params.get('groupType', 'PERSONAL')
    exclude_chat_ids = set(params.get('excludeChatIds', []))
    messages_count = params.get('messagesCount', 0)

    result = []
    me = await client.get_me()

    async for d in client.iter_dialogs():
        if len(result) >= max_found_count:
            break

        # Исключаем чаты из списка
        if d.id in exclude_chat_ids:
            continue

        # Фильтр по ботам (3 варианта)
        is_bot = hasattr(d.entity, 'bot') and d.entity.bot
        if bot_type == 'PERSONAL' and is_bot:
            continue
        elif bot_type == 'NOT_PERSONAL' and not is_bot:
            continue

        # Фильтр по группам/каналам (3 варианта)
        is_group = (hasattr(d.entity, 'broadcast') and d.entity.broadcast) or \
                   (hasattr(d.entity, 'megagroup') and d.entity.megagroup)

        if group_type == 'PERSONAL' and is_group:
            continue
        elif group_type == 'NOT_PERSONAL' and not is_group:
            continue

        # Фильтр по давности
        if min_diff_days_count and min_diff_days_count > 0 and d.message and d.message.date:
            last_date = d.message.date.replace(tzinfo=None)
            now = datetime.now()
            days_ago = (now - last_date).days
            if days_ago < min_diff_days_count:
                continue

        # Поиск по ключевому слову
        try:
            async for m in client.iter_messages(d.id, search=term, limit=1):
                if m.text and term.lower() in m.text.lower():
                    # Получаем username или телефон
                    username = getattr(d.entity, 'username', None)
                    if not username:
                        phone = getattr(d.entity, 'phone', None)
                        if phone:
                            username = phone

                    chat_info = {
                        'id': d.id,
                        'name': d.name,
                        'username': username
                    }

                    # Добавляем последние сообщения, если messages_count > 0
                    if messages_count > 0:
                        messages = []
                        async for msg in client.iter_messages(d.id, limit=messages_count):
                            if msg.date:
                                messages.append({
                                    'value': msg.text if msg.text else '',
                                    'dateTime': msg.date.isoformat().replace('+00:00', ''),
                                    'ownerFlag': msg.sender_id == me.id
                                })
                        messages.reverse()
                        chat_info['messages'] = messages

                    result.append(chat_info)
                    print(f"✅ Найдено: {d.name} (username: {username})")
                    break
        except Exception as e:
            print(f"Ошибка в чате {d.name}: {e}")
            continue

    print(f"🔵 Поиск завершён. Найдено чатов: {len(result)}")
    return result


async def get_chats_info(client, chat_ids):
    """
    Получает информацию о чатах по их ID.
    """
    results = []

    for chat_id in chat_ids:
        try:
            if isinstance(chat_id, str):
                chat_id = int(chat_id)

            entity = await client.get_entity(chat_id)

            name = None
            if hasattr(entity, 'title'):
                name = entity.title
            elif hasattr(entity, 'first_name'):
                name = entity.first_name
                if hasattr(entity, 'last_name') and entity.last_name:
                    name += " " + entity.last_name

            username = getattr(entity, 'username', None)
            if not username:
                phone = getattr(entity, 'phone', None)
                if phone:
                    username = phone

            results.append({
                'id': chat_id,
                'username': username,
                'name': name
            })

            print(f"✅ Получена информация о чате {chat_id}: {name} (username: {username})")

        except (ValueError, UserIdInvalidError) as e:
            results.append({
                'id': chat_id,
                'username': None,
                'name': None,
                'error': str(e)
            })
            print(f"❌ Ошибка: чат {chat_id} не найден")

        except (ChatAdminRequiredError, ChannelPrivateError) as e:
            results.append({
                'id': chat_id,
                'username': None,
                'name': None,
                'error': f"Нет доступа: {str(e)}"
            })
            print(f"⚠️ Нет доступа к чату {chat_id}")

        except Exception as e:
            results.append({
                'id': chat_id,
                'username': None,
                'name': None,
                'error': str(e)
            })
            print(f"❌ Ошибка при получении чата {chat_id}: {e}")

    return results