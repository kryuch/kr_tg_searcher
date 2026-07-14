from telethon.tl.types import User
import asyncio
import logging

logger = logging.getLogger(__name__)


def group_chats_by_account(data):
    """
    Группирует чаты по tgAccountId из запроса.
    Используется в эндпоинте /api/send_bulk_messages.

    Args:
        data: dict с данными запроса, содержащий 'contacts.chatIds'

    Returns:
        dict: {tg_account_id: [chat_id1, chat_id2, ...]}
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
            logger.warning("Пропускаем чат: нет id")
            continue

        if not tg_account_id:
            logger.warning(f"Пропускаем чат {chat_id}: нет tgAccountId")
            continue

        if tg_account_id not in chats_by_account:
            chats_by_account[tg_account_id] = []
        chats_by_account[tg_account_id].append(chat_id)

    return chats_by_account


async def send_messages(client, chat_ids, message_text, delay, only_new_chats=False):
    """
    Отправка сообщений. Поддерживает как числовые ID, так и username.
    """
    results = []
    sent_count = 0

    for i, chat_id in enumerate(chat_ids):
        try:
            # Если chat_id — это число, получаем entity
            if isinstance(chat_id, (int, str)):
                try:
                    # Пробуем получить entity по ID
                    entity = await client.get_entity(chat_id)
                except Exception as e:
                    results.append({
                        'id': chat_id,
                        'name': str(chat_id),
                        'username': None,
                        'status': 'error',
                        'error': f'Не удалось найти чат: {e}'
                    })
                    print(f"❌ [{i+1}/{len(chat_ids)}] Чат {chat_id} не найден")
                    continue
            else:
                # Если уже entity
                entity = chat_id

            # Проверяем, существует ли уже чат (только если only_new_chats=True)
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
                                'error': 'Чат уже существует'
                            })
                            print(f"⏭️ [{i+1}/{len(chat_ids)}] Пропущен {chat_id} ({entity.id}) (чат уже существует)")
                            break
                except Exception as e:
                    print(f"⚠️ Ошибка проверки диалога для {chat_id}: {e}")

                if existing:
                    continue

            # Отправляем сообщение
            await client.send_message(entity, message_text)

            chat_info = {
                'id': entity.id,
                'name': getattr(entity, 'first_name', getattr(entity, 'title', str(chat_id))),
                'username': getattr(entity, 'username', None),
                'status': 'success'
            }
            results.append(chat_info)
            sent_count += 1
            print(f"✅ [{i+1}/{len(chat_ids)}] Отправлено в {chat_info['name']} (ID: {chat_info['id']})")

            if sent_count > 0 and i < len(chat_ids) - 1:
                await asyncio.sleep(delay)

        except Exception as e:
            results.append({
                'id': chat_id if isinstance(chat_id, (int, str)) else None,
                'name': str(chat_id),
                'username': chat_id if isinstance(chat_id, str) else None,
                'status': 'error',
                'error': str(e)
            })
            print(f"❌ Ошибка отправки в {chat_id}: {e}")

    print(f"\n📊 Итог: отправлено {sent_count} из {len(chat_ids)} сообщений")
    return results