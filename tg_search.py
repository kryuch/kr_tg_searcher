import asyncio
import base64
from datetime import datetime
from io import BytesIO
from telethon.errors import ChatAdminRequiredError, ChannelPrivateError, UserIdInvalidError


async def get_avatar(client, entity):
    """Асинхронное получение аватара в base64."""
    try:
        photo = await client.get_profile_photos(entity, limit=1)
        if photo:
            buffer = BytesIO()
            await client.download_media(photo[0], file=buffer)
            if buffer.getvalue():
                return base64.b64encode(buffer.getvalue()).decode('utf-8')
    except Exception as e:
        print(f"Avatar error: {e}")
    return None


def prepare_search_params(data):
    """
    Подготавливает параметры поиска из запроса.
    """
    # Обрабатываем excludeChats
    exclude_chats = {}
    for item in data.get('excludeChats', []):
        tg_account_id = item.get('tgAccountId')
        user_id = item.get('userId')
        if tg_account_id is not None and user_id is not None:
            if tg_account_id not in exclude_chats:
                exclude_chats[tg_account_id] = set()
            exclude_chats[tg_account_id].add(user_id)

    term = data.get('term')
    term = term.strip() if term else 'Java'

    last_message = data.get('lastMessage')
    last_message = last_message.strip() if last_message else ''

    last_message_type = data.get('lastMessageType', 0)
    if last_message_type is None:
        last_message_type = 0

    if isinstance(last_message_type, str):
        if last_message_type == 'EXCLUDE':
            last_message_type = -1
        elif last_message_type == 'ONLY':
            last_message_type = 1
        elif last_message_type == 'IGNORE':
            last_message_type = 0
        else:
            try:
                last_message_type = int(last_message_type)
            except ValueError:
                last_message_type = 0

    return {
        'term': term,
        'lastMessage': last_message,
        'lastMessageType': last_message_type,
        'maxFoundCount': data.get('maxFoundCount', 10) or 10,
        'minDiffDaysCount': data.get('minDiffDaysCount', 0) if data.get('minDiffDaysCount') is not None else 0,
        'botType': data.get('botType', 'PERSONAL'),
        'groupType': data.get('groupType', 'PERSONAL'),
        'excludeChats': exclude_chats,
        'messagesCount': data.get('messagesCount', 0) or 0
    }


async def search_chats(client, params, account_id):
    """
    Поиск чатов по параметрам с учётом исключений для конкретного аккаунта.

    Args:
        client: Telethon клиент
        params: параметры поиска
        account_id: ID аккаунта (для фильтрации исключений)
    """
    term = params.get('term', 'Java')
    last_message = params.get('lastMessage', '').strip()
    max_found_count = params.get('maxFoundCount', 16)
    last_message_type = params.get('lastMessageType', 0)
    min_diff_days_count = params.get('minDiffDaysCount', 0)
    bot_type = params.get('botType', 'PERSONAL')
    group_type = params.get('groupType', 'PERSONAL')
    exclude_chats = params.get('excludeChats', {})
    messages_count = params.get('messagesCount', 0)

    # Получаем список исключённых ID для этого аккаунта
    exclude_ids = exclude_chats.get(account_id, set())
    print(f"🔵 Исключения для аккаунта {account_id}: {len(exclude_ids)} чатов")

    result = []
    me = await client.get_me()

    async for d in client.iter_dialogs():
        if len(result) >= max_found_count:
            break

        # Проверяем исключения для этого аккаунта
        if d.id in exclude_ids:
            continue

        # Фильтр по ботам
        is_bot = hasattr(d.entity, 'bot') and d.entity.bot
        if bot_type == 'PERSONAL' and is_bot:
            continue
        elif bot_type == 'NOT_PERSONAL' and not is_bot:
            continue

        # Фильтр по группам/каналам
        is_group = (hasattr(d.entity, 'broadcast') and d.entity.broadcast) or \
                   (hasattr(d.entity, 'megagroup') and d.entity.megagroup)

        if group_type == 'PERSONAL' and is_group:
            continue
        elif group_type == 'NOT_PERSONAL' and not is_group:
            continue

        # Фильтр по давности
        if min_diff_days_count and min_diff_days_count > 0:
            try:
                if d.message is None or d.message.date is None:
                    continue

                last_date = d.message.date.replace(tzinfo=None)
                now = datetime.now()
                days_ago = (now - last_date).days

                if days_ago is not None and days_ago < min_diff_days_count:
                    continue
            except Exception as e:
                print(f"Ошибка фильтра давности для {d.name}: {e}")
                continue

        # Поиск по ключевому слову
        try:
            async for m in client.iter_messages(d.id, search=term, limit=1):
                if m.text and term.lower() in m.text.lower():
                    if last_message_type != 0:
                       try:
                           last_msg = None
                           async for msg in client.iter_messages(d.id, limit=1):
                               last_msg = msg
                               break

                           last_msg_text = last_msg.text if last_msg and last_msg.text else ''
                           last_message_lower = last_message.lower()
                           last_msg_text_lower = last_msg_text.lower()

                           if last_message_type == 1:
                               if last_message_lower not in last_msg_text_lower:
                                   continue
                           elif last_message_type == -1:
                               if last_message_lower in last_msg_text_lower:
                                   continue
                       except Exception as e:
                           print(f"Ошибка при проверке lastMessage для {d.name}: {e}")
                           continue

                    username = getattr(d.entity, 'username', None)
                    if not username:
                        phone = getattr(d.entity, 'phone', None)
                        if phone:
                            username = phone

                    # УДАЛЕНО: avatar = await get_avatar(client, d.entity)

                    chat_info = {
                        'id': d.id,
                        'name': d.name,
                        'username': username
                        # УДАЛЕНО: 'avatar': avatar
                    }

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

    print(f"🔵 Поиск завершён для аккаунта {account_id}. Найдено чатов: {len(result)}")
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

            avatar = await get_avatar(client, entity)

            results.append({
                'id': chat_id,
                'username': username,
                'name': name,
                'avatar': avatar
            })

            print(f"✅ Получена информация о чате {chat_id}: {name} (username: {username})")

        except (ValueError, UserIdInvalidError) as e:
            results.append({
                'id': chat_id,
                'username': None,
                'name': None,
                'avatar': None,
                'error': str(e)
            })
            print(f"❌ Ошибка: чат {chat_id} не найден")

        except (ChatAdminRequiredError, ChannelPrivateError) as e:
            results.append({
                'id': chat_id,
                'username': None,
                'name': None,
                'avatar': None,
                'error': f"Нет доступа: {str(e)}"
            })
            print(f"⚠️ Нет доступа к чату {chat_id}")

        except Exception as e:
            results.append({
                'id': chat_id,
                'username': None,
                'name': None,
                'avatar': None,
                'error': str(e)
            })
            print(f"❌ Ошибка при получении чата {chat_id}: {e}")

    return results