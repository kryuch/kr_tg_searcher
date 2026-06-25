async def get_chat_preview(client, chat_id, limit=10):
    messages = []
    me = await client.get_me()

    try:
        async for msg in client.iter_messages(chat_id, limit=limit):
            if msg.date:
                messages.append({
                    'value': msg.text if msg.text else '',
                    'dateTime': msg.date.isoformat().replace('+00:00', ''),
                    'ownerFlag': msg.sender_id == me.id
                })
    except Exception as e:
        print(f"Ошибка получения сообщений: {e}")
        return {'messages': []}

    messages.reverse()
    return {'messages': messages}