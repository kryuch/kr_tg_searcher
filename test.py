import asyncio
from telethon import TelegramClient
import os

API_ID = int(os.getenv("API_ID", 30101072))
API_HASH = os.getenv("API_HASH", "e0f3d73a25cc1c3084fb3341fb526955")
PHONE = os.getenv("PHONE", "+79204962556")

async def test_folders():
    client = TelegramClient('test_session', API_ID, API_HASH)
    await client.start(PHONE)

    # ID чата, который нужно проверить
    CHAT_ID = 6444030261  # a_Irina

    print("=" * 50)
    print(f"🔍 Проверяем чат с ID: {CHAT_ID}")
    print("=" * 50)

    # 1. Проверяем, есть ли чат в диалогах
    print("\n1. Получаем все диалоги...")
    dialogs = await client.get_dialogs()

    found_chat = None
    for d in dialogs:
        if d.id == CHAT_ID:
            found_chat = d
            print(f"✅ Чат найден: {d.name}")
            print(f"   folder_id: {d.folder_id}")
            break

    if not found_chat:
        print("❌ Чат не найден в диалогах")
        return

    # 2. Получаем все папки
    print("\n2. Получаем все папки...")
    try:
        folders = await client.get_folders()
        print(f"   Всего папок: {len(folders)}")
        for f in folders:
            print(f"   - {f.id}: {f.title}")
    except Exception as e:
        print(f"   ❌ Ошибка get_folders: {e}")
        folders = []

    # 3. Проверяем, в каких папках есть чат
    print("\n3. Проверяем папки для чата...")
    chat_folders = []

    for folder in folders:
        try:
            print(f"   Проверяем папку: {folder.title} (ID: {folder.id})")
            folder_dialogs = await client.get_dialogs(folder=folder.id)
            for d in folder_dialogs:
                if d.id == CHAT_ID:
                    chat_folders.append({
                        'id': folder.id,
                        'title': folder.title
                    })
                    print(f"   ✅ Чат найден в папке: {folder.title}")
                    break
        except Exception as e:
            print(f"   ⚠️ Ошибка при получении диалогов для папки {folder.id}: {e}")

    # 4. Результат
    print("\n" + "=" * 50)
    print("📁 РЕЗУЛЬТАТ:")
    print(f"   Чат: {found_chat.name}")
    print(f"   folder_id из диалога: {found_chat.folder_id}")
    print(f"   Найден в папках: {chat_folders if chat_folders else 'НЕТ'}")
    print("=" * 50)

    await client.disconnect()

if __name__ == "__main__":
    asyncio.run(test_folders())