import asyncio
import logging
from telethon.tl.types import User

logger = logging.getLogger(__name__)


def group_chats_by_account(data, client=None):
    """
    Группирует чаты по tgAccountId из запроса.
    """
    chats_by_account = {}
    contacts_data = data.get('contacts', {})

    if not contacts_data:
        return chats_by_account

    chat_ids_data = contacts_data.get('chatIds', [])
    for chat_item in chat_ids_data:
        chat_id = chat_item.get('id')
        tg_account_id_raw = chat_item.get('tgAccountId')
        tg_account_id = str(tg_account_id_raw) if tg_account_id_raw is not None else None

        if not chat_id:
            continue

        if not tg_account_id:
            continue

        if tg_account_id not in chats_by_account:
            chats_by_account[tg_account_id] = []
        chats_by_account[tg_account_id].append(chat_id)

    return chats_by_account


async def process_and_send_messages(data, run_async_func, account_configs, execute_telegram_action_func):
    """
    Обрабатывает запрос на отправку сообщений.
    Поддерживает два формата:
    1. contacts.chatIds - список ID чатов
    2. contactUsernames - список username
    """
    message_text = data.get('messageText', '')
    delay = data.get('delaySeconds', 2)
    only_new_chats = data.get('onlyNewChats', False)

    if not message_text:
        return {
            'total': 0,
            'success': 0,
            'skipped': 0,
            'error': 0,
            'results': [],
            'error': 'Нет текста сообщения'
        }

    # ============================================================
    # 1. ОБРАБОТКА contactUsernames
    # ============================================================
    contact_usernames = data.get('contactUsernames', [])
    chat_ids_found = []
    not_found_usernames = []

    if contact_usernames:
        tg_account_id = str(data.get('tgAccountId'))

        if tg_account_id in account_configs:
            async def find_chats(client):
                chats = []
                not_found = []
                for username in contact_usernames:
                    try:
                        clean_username = username.lstrip('@')
                        entity = await client.get_entity(clean_username)
                        chats.append(entity.id)
                        print(f"   ✅ Найден чат для {username}: {entity.id}")
                    except Exception as e:
                        not_found.append({
                            'username': username,
                            'error': str(e)
                        })
                        print(f"   ⚠️ Ошибка при поиске {username}: {e}")
                return chats, not_found

            chat_ids_found, not_found_usernames = await execute_telegram_action_func(
                tg_account_id, account_configs, find_chats
            )
            print(f"🔵 Найдено чатов: {len(chat_ids_found)}, не найдено: {len(not_found_usernames)}")

    # ============================================================
    # 2. ГРУППИРОВКА ЧАТОВ (contacts.chatIds + найденные по username)
    # ============================================================
    chats_by_account = {}

    # Добавляем найденные по username
    if chat_ids_found:
        tg_account_id = str(data.get('tgAccountId', '1'))
        if tg_account_id not in chats_by_account:
            chats_by_account[tg_account_id] = []
        chats_by_account[tg_account_id].extend(chat_ids_found)

    # Добавляем из contacts.chatIds
    contacts_data = data.get('contacts', {})
    if contacts_data:
        chat_ids_data = contacts_data.get('chatIds', [])
        for chat_item in chat_ids_data:
            chat_id = chat_item.get('id')
            tg_account_id = str(chat_item.get('tgAccountId', '1'))
            if chat_id:
                if tg_account_id not in chats_by_account:
                    chats_by_account[tg_account_id] = []
                chats_by_account[tg_account_id].append(chat_id)

    if not chats_by_account and not not_found_usernames:
        return {
            'total': 0,
            'success': 0,
            'skipped': 0,
            'error': 0,
            'results': []
        }

    # ============================================================
    # 3. ОТПРАВКА СООБЩЕНИЙ (используем старую добрую send_messages)
    # ============================================================
    all_results = []
    total_success = 0
    total_skipped = 0
    total_error = 0

    for tg_account_id, chat_ids in chats_by_account.items():
        print(f"🔵 Отправка от аккаунта {tg_account_id} в {len(chat_ids)} чатов")

        results = await execute_telegram_action_func(
            tg_account_id,
            account_configs,
            send_messages,  # <-- используем исходную функцию
            chat_ids,
            message_text,
            delay,
            only_new_chats
        )

        for result in results:
            result['tgAccountId'] = tg_account_id
            all_results.append(result)

        success_count = len([r for r in results if r['status'] == 'success'])
        skipped_count = len([r for r in results if r['status'] == 'skipped'])
        error_count = len([r for r in results if r['status'] == 'error'])
        total_success += success_count
        total_skipped += skipped_count
        total_error += error_count

        print(f"   ✅ Отправлено: success={success_count}, skipped={skipped_count}, error={error_count}")

    # ============================================================
    # 4. ДОБАВЛЯЕМ НЕ НАЙДЕННЫХ
    # ============================================================
    for not_found in not_found_usernames:
        all_results.append({
            'id': None,
            'name': not_found['username'],
            'username': not_found['username'],
            'status': 'error',
            'error': 'USER_NOT_FOUND',
            'comment': f"Пользователь не найден: {not_found['error']}"
        })
        total_error += 1

    print(f"🔵 Итог: total={len(all_results)}, success={total_success}, skipped={total_skipped}, error={total_error}")

    return {
        'total': len(all_results),
        'success': total_success,
        'skipped': total_skipped,
        'error': total_error,
        'results': all_results
    }


async def send_messages(client, chat_ids, message_text, delay, only_new_chats=False):
    """
    Отправка сообщений. Поддерживает как числовые ID, так и username.
    Возвращает:
        - status: 'success' | 'skipped' | 'error'
        - error: код ошибки (если status='error')
        - comment: человекочитаемое описание (если status='error' или 'skipped')
    """
    results = []
    sent_count = 0

    for i, chat_id in enumerate(chat_ids):
        try:
            if isinstance(chat_id, (int, str)):
                try:
                    entity = await client.get_entity(chat_id)
                except Exception as e:
                    results.append({
                        'id': chat_id,
                        'name': str(chat_id),
                        'username': None,
                        'status': 'error',
                        'error': 'USER_NOT_FOUND',
                        'comment': f'Не удалось найти чат: {e}'
                    })
                    print(f"❌ [{i+1}/{len(chat_ids)}] Чат {chat_id} не найден")
                    continue
            else:
                entity = chat_id

            if only_new_chats:
                existing = False
                try:
                    async for d in client.iter_dialogs():
                        if d.entity.id == entity.id:
                            existing = True
                            results.append({
                                'id': entity.id,
                                'name': getattr(entity, 'first_name', getattr(entity, 'title', str(chat_id))),
                                'username': getattr(entity, 'username', None),
                                'status': 'skipped',
                                'error': None,
                                'comment': 'Чат уже существует'
                            })
                            print(f"⏭️ [{i+1}/{len(chat_ids)}] Пропущен {chat_id} ({entity.id}) (чат уже существует)")
                            break
                except Exception as e:
                    print(f"⚠️ Ошибка проверки диалога для {chat_id}: {e}")

                if existing:
                    continue

            await client.send_message(entity, message_text)

            chat_info = {
                'id': entity.id,
                'name': getattr(entity, 'first_name', getattr(entity, 'title', str(chat_id))),
                'username': getattr(entity, 'username', None),
                'status': 'success',
                'error': None,
                'comment': None
            }
            results.append(chat_info)
            sent_count += 1
            print(f"✅ [{i+1}/{len(chat_ids)}] Отправлено в {chat_info['name']} (ID: {chat_info['id']})")

            if sent_count > 0 and i < len(chat_ids) - 1:
                await asyncio.sleep(delay)

        except Exception as e:
            error_msg = str(e)
            error_code = None
            comment = error_msg

            if "invalid peer" in error_msg.lower():
                error_code = 'INVALID_PEER'
                comment = 'Некорректный получатель'
            elif "user is blocked" in error_msg.lower():
                error_code = 'USER_BLOCKED'
                comment = 'Пользователь заблокировал вас'
            elif "user is deleted" in error_msg.lower():
                error_code = 'USER_DELETED'
                comment = 'Пользователь удалил аккаунт'
            elif "bot cannot start conversation" in error_msg.lower():
                error_code = 'BOT_CANNOT_START'
                comment = 'Нельзя начать диалог с ботом'
            elif "flood" in error_msg.lower():
                error_code = 'MANY_REQUESTS'
                comment = 'Слишком много запросов'
            elif "chat admin required" in error_msg.lower():
                error_code = 'ADMIN_REQUIRED'
                comment = 'Требуются права администратора'
            elif "not enough rights" in error_msg.lower():
                error_code = 'NOT_ENOUGH_RIGHTS'
                comment = 'Недостаточно прав'
            elif "you are not a member" in error_msg.lower():
                error_code = 'NOT_MEMBER'
                comment = 'Вы не являетесь участником чата'
            elif "user not found" in error_msg.lower():
                error_code = 'USER_NOT_FOUND'
                comment = 'Пользователь не найден'
            elif "chat not found" in error_msg.lower():
                error_code = 'CHAT_NOT_FOUND'
                comment = 'Чат не найден'
            else:
                error_code = 'UNKNOWN_ERROR'
                comment = error_msg

            results.append({
                'id': chat_id if isinstance(chat_id, (int, str)) else None,
                'name': str(chat_id),
                'username': chat_id if isinstance(chat_id, str) else None,
                'status': 'error',
                'error': error_code,
                'comment': comment
            })
            print(f"❌ Ошибка отправки в {chat_id}: {error_code} - {comment}")

    print(f"\n📊 Итог: отправлено {sent_count} из {len(chat_ids)} сообщений")
    return results