from telethon.tl.types import User
import asyncio

async def send_messages(client, chat_ids, message_text, delay, only_new_chats=False):
    """
    Отправка сообщений. Поддерживает как числовые ID, так и username.
    Возвращает список результатов с информацией о чатах.

    Параметры:
    - delay: пауза между отправками (в секундах)
    - only_new_chats: если True, отправляет сообщения только если чата ещё нет
    """
    results = []
    sent_count = 0  # Счётчик реально отправленных сообщений

    try:
        delay = int(delay)
    except (TypeError, ValueError):
        delay = 4

    for i, chat_id in enumerate(chat_ids):
        try:
            # Определяем тип chat_id (число или строка)
            if isinstance(chat_id, str):
                try:
                    entity = await client.get_entity(chat_id)
                except Exception as e:
                    results.append({
                        'id': None,
                        'name': chat_id,
                        'username': chat_id,
                        'status': 'error',
                        'error': f'Пользователь {chat_id} не найден'
                    })
                    print(f"❌ [{i+1}/{len(chat_ids)}] Пользователь {chat_id} не найден")
                    continue
            else:
                entity = chat_id

            # Проверяем, существует ли уже чат (только если only_new_chats=True)
            should_skip = False
            if only_new_chats:
                try:
                    async for d in client.iter_dialogs():
                        if d.entity.id == entity.id:
                            results.append({
                                'id': entity.id,
                                'name': getattr(entity, 'first_name', getattr(entity, 'title', str(chat_id))),
                                'username': getattr(entity, 'username', None),
                                'status': 'skipped',
                                'error': 'Чат уже существует'
                            })
                            print(f"⏭️ [{i+1}/{len(chat_ids)}] Пропущен {chat_id} (этот чат уже существует)")
                            should_skip = True
                            break
                except Exception as e:
                    print(f"⚠️ Ошибка проверки диалога для {chat_id}: {e}")

            if should_skip:
                continue

            # Отправляем сообщение
            await client.send_message(entity, message_text)

            # Собираем информацию о чате
            chat_info = {
                'id': entity.id,
                'name': getattr(entity, 'first_name', getattr(entity, 'title', str(chat_id))),
                'username': getattr(entity, 'username', None),
                'status': 'success'
            }
            results.append(chat_info)
            sent_count += 1
            print(f"✅ [{i+1}/{len(chat_ids)}] Отправлено в {chat_info['name']} (ID: {chat_info['id']})")

            # Пауза только если были отправки и это не последний чат
            if sent_count > 0 and i < len(chat_ids) - 1:
                await asyncio.sleep(delay)

        except Exception as e:
            results.append({
                'id': None,
                'name': str(chat_id),
                'username': chat_id if isinstance(chat_id, str) else None,
                'status': 'error',
                'error': str(e)
            })
            print(f"❌ Ошибка отправки в {chat_id}: {e}")

    print(f"\n📊 Итог: отправлено {sent_count} из {len(chat_ids)} сообщений")
    return results