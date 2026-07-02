python
from telethon import TelegramClient
import asyncio
import os

API_ID = int(os.getenv("API_ID", 30101072))
API_HASH = os.getenv("API_HASH", "e0f3d73a25cc1c3084fb3341fb526955")
PHONE = os.getenv("PHONE", "+79204962556")

async def main():
    client = TelegramClient('test_session', API_ID, API_HASH)
    await client.start(PHONE)

    chat_id = 6444030261

    # Получаем диалог
    dialogs = await client.get_dialogs()
    for d in dialogs:
        if d.id == chat_id:
            print(f"Чат: {d.name}")
            print(f"ID: {d.id}")
            print(f"folder_id: {d.folder_id}")
            print(f"Тип: {type(d.entity)}")
            print(f"Все атрибуты: {dir(d)}")
            break

    # Получаем все папки
    from telethon.tl.functions.messages import GetDialogFiltersRequest
    folders = await client(GetDialogFiltersRequest())
    print(f"\nПапки: {folders}")

    await client.disconnect()

asyncio.run(main())