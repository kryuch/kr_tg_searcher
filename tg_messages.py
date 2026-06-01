async def get_chat_preview(client, chat_id, limit=10):
    messages = []
    me = await client.get_me()

    try:
        async for msg in client.iter_messages(chat_id, limit=limit):
            if msg.date:
                messages.append({
                    'text': msg.text if msg.text else '',  # убрано [:200]
                    'date_str': msg.date.strftime('%Y-%m-%d %H:%M:%S'),
                    'is_me': msg.sender_id == me.id
                })
    except Exception as e:
        print(f"Ошибка получения сообщений: {e}")
        return {'messages': []}

    messages.reverse()
    return {'messages': messages}
