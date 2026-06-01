import asyncio
from datetime import datetime

async def search_chats(client, params):
    """
    Поиск чатов по параметрам (соответствует Java SearchParams)
    
    params: {
        'term': str,                 # ключевое слово (по умолчанию "Java")
        'maxFoundCount': int,        # максимальное количество чатов (по умолчанию 10)
        'minDiffDaysCount': int,     # минимальное количество дней без сообщений (по умолчанию 7)
        'excludeBotFlag': bool,      # исключить ботов (по умолчанию True)
        'excludeGroupFlag': bool,    # исключить группы (по умолчанию True)
        'excludeStatusFlag': bool    # исключить по статусу (пока не используется)
    }
    """
    term = params.get('term', 'Java')
    max_found_count = params.get('maxFoundCount', 10)
    min_diff_days_count = params.get('minDiffDaysCount', 7)
    exclude_bot_flag = params.get('excludeBotFlag', True)
    exclude_group_flag = params.get('excludeGroupFlag', True)
    # exclude_status_flag = params.get('excludeStatusFlag', True)  # пока не используется

    result = []
    async for d in client.iter_dialogs():
        if len(result) >= max_found_count:
            break

        # Фильтр по типу чата (исключаем группы)
        if exclude_group_flag:
            if hasattr(d.entity, 'broadcast') and d.entity.broadcast:
                continue
            if hasattr(d.entity, 'megagroup') and d.entity.megagroup:
                continue

        # Фильтр по ботам
        if exclude_bot_flag and hasattr(d.entity, 'bot') and d.entity.bot:
            continue

        # Фильтр по давности (если указан)
        if min_diff_days_count and min_diff_days_count > 0:
            try:
                last_msg = await client.get_messages(d.id, limit=1)
                if last_msg and last_msg[0].date:
                    last_date = last_msg[0].date.replace(tzinfo=None)
                    now = datetime.now()
                    days_ago = (now - last_date).days
                    if days_ago < min_diff_days_count:
                        continue
            except Exception as e:
                print(f"Ошибка фильтра давности для {d.name}: {e}")

        # Поиск по ключевому слову
        try:
            async for m in client.iter_messages(d.id, limit=30):
                if m.text and term.lower() in m.text.lower():
                    result.append({
                        'id': d.id,
                        'name': d.name
                    })
                    print(f"✅ Найдено: {d.name}")
                    break
        except Exception as e:
            print(f"Ошибка в чате {d.name}: {e}")
            continue

    print(f"🔵 Поиск завершён. Найдено чатов: {len(result)}")
    return result