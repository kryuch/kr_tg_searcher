from telethon.tl.types import User
import asyncio

async def send_messages(client, chat_ids, message_text, delay, only_new_chats=False):
    """
    Отправка сообщений. Поддерживает как числовые ID, так и username.
    Возвращает список результатов с информацией о чатах.

    Параметры:
    - only_new_chats: если True, отправляет сообщения только если чат существует (username найден)
                      если False, отправляет всем (создавая новые чаты)
    """
    results = []
    for i, chat_id in enumerate(chat_ids):
        try:
            # Определяем тип chat_id (число или строка)
            if isinstance(chat_id, str):
                # Если это username
                try:
                    entity = await client.get_entity(chat_id)
                except Exception as e:
                    # Если пользователь не найден
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

            # Если only_new_chats = True, проверяем, существует ли уже чат
            if only_new_chats:
                # Проверяем, существует ли диалог с этим пользователем
                try:
                    # Пытаемся получить диалог
                    dialog = await client.get_dialogs()
                    existing_chat = None
                    for d in dialog:
                        if d.entity.id == entity.id:
                            existing_chat = d
                            break

                    if existing_chat:
                        results.append({
                            'id': entity.id,
                            'name': getattr(entity, 'first_name', getattr(entity, 'title', str(chat_id))),
                            'username': getattr(entity, 'username', None),
                            'status': 'skipped',
                            'error': 'Чат уже существует'
                        })
                        print(f"⏭️ [{i+1}/{len(chat_ids)}] Пропущен {chat_id} (чат уже существует)")
                        continue
                except Exception as e:
                    print(f"⚠️ Ошибка проверки диалога для {chat_id}: {e}")

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
            print(f"✅ [{i+1}/{len(chat_ids)}] Отправлено в {chat_info['name']} (ID: {chat_info['id']})")
            
            if i < len(chat_ids) - 1:
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
    
    return results