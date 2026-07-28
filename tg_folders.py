import asyncio
import logging
from telethon.tl.functions.messages import GetDialogFiltersRequest, UpdateDialogFilterRequest
from telethon.errors import FloodWaitError

logger = logging.getLogger(__name__)


def _peer_id(peer):
    return (
        getattr(peer, "user_id", None)
        or getattr(peer, "channel_id", None)
        or getattr(peer, "chat_id", None)
    )
from telethon.tl.types import DialogFilter, TextWithEntities
from telethon.tl.functions.messages import (
    GetDialogFiltersRequest,
    UpdateDialogFilterRequest,
)

from telethon.tl.types import DialogFilter, TextWithEntities
from telethon.tl.functions.messages import (
    GetDialogFiltersRequest,
    UpdateDialogFilterRequest,
)

async def create_folder(client, title, chat_ids=None):
    try:
        result = await client(GetDialogFiltersRequest())
        filters = result.filters if hasattr(result, "filters") else list(result)

        max_id = max((getattr(f, "id", 0) for f in filters), default=0)
        new_folder_id = max_id + 1

        title_obj = TextWithEntities(text=title, entities=[])

        include_peers = []

        if chat_ids:
            for chat_id in chat_ids:
                peer = await client.get_input_entity(chat_id)
                include_peers.append(peer)

        # =====================================================
        # Если чатов нет — используем Saved Messages как временный
        # =====================================================

        temp_peer = None

        if not include_peers:
            me = await client.get_me()
            temp_peer = await client.get_input_entity(me.id)
            include_peers = [temp_peer]

        # =====================================================
        # 1. Создаем папку
        # =====================================================

        await client(
            UpdateDialogFilterRequest(
                id=new_folder_id,
                filter=DialogFilter(
                    id=new_folder_id,
                    title=title_obj,
                    pinned_peers=[],
                    include_peers=include_peers,
                    exclude_peers=[],
                    contacts=False,
                    non_contacts=False,
                    groups=False,
                    broadcasts=False,
                    bots=False,
                    exclude_muted=False,
                    exclude_read=False,
                    exclude_archived=False,
                    emoticon=None,
                ),
            )
        )

        # =====================================================
        # 2. Если использовали временный чат — очищаем папку
        # =====================================================

        if temp_peer is not None:
            await client(
                UpdateDialogFilterRequest(
                    id=new_folder_id,
                    filter=DialogFilter(
                        id=new_folder_id,
                        title=title_obj,
                        pinned_peers=[],
                        include_peers=[],
                        exclude_peers=[],
                        contacts=False,
                        non_contacts=False,
                        groups=False,
                        broadcasts=False,
                        bots=False,
                        exclude_muted=False,
                        exclude_read=False,
                        exclude_archived=False,
                        emoticon=None,
                    ),
                )
            )

        return {
            "success": True,
            "folder_id": new_folder_id,
            "title": title,
            "chat_ids": chat_ids or [],
            "error": None,
        }

    except Exception as e:
        return {
            "success": False,
            "folder_id": None,
            "title": title,
            "chat_ids": chat_ids or [],
            "error": str(e),
        }

async def update_chat_folders(client, folder_id, chat_ids, add_to_folder):
    try:
        result = await client(GetDialogFiltersRequest())
        filters = result.filters if hasattr(result, "filters") else list(result)

        folder = next(
            (f for f in filters if getattr(f, "id", None) == folder_id),
            None
        )

        if folder is None:
            return {
                "success": False,
                "error": f"Folder {folder_id} not found"
            }

        include_peers = list(folder.include_peers)
        current_ids = {_peer_id(peer) for peer in include_peers}
        results = []

        for chat_id in chat_ids:
            try:
                peer = await client.get_input_entity(chat_id)

                if add_to_folder:
                    if chat_id not in current_ids:
                        include_peers.append(peer)
                        current_ids.add(chat_id)
                else:
                    include_peers = [
                        p for p in include_peers
                        if _peer_id(p) != chat_id
                    ]
                    current_ids.discard(chat_id)

                results.append({
                    "chat_id": chat_id,
                    "status": "success"
                })

            except FloodWaitError as e:
                await asyncio.sleep(e.seconds)
                results.append({
                    "chat_id": chat_id,
                    "status": "error",
                    "error": f"FloodWait {e.seconds}"
                })

            except Exception as e:
                logger.exception("Cannot process chat %s", chat_id)
                results.append({
                    "chat_id": chat_id,
                    "status": "error",
                    "error": str(e)
                })

        # Обновляем фильтр через сырой запрос (без clone_filter, который использует DialogFilter)
        new_filter = InputDialogFilter(
            id=folder.id,
            title=folder.title,
            pinned_peers=folder.pinned_peers,
            include_peers=include_peers,
            exclude_peers=folder.exclude_peers,
            contacts=folder.contacts,
            non_contacts=folder.non_contacts,
            groups=folder.groups,
            broadcasts=folder.broadcasts,
            bots=folder.bots,
            exclude_muted=folder.exclude_muted,
            exclude_read=folder.exclude_read,
            exclude_archived=folder.exclude_archived
        )

        await client._call(UpdateDialogFilterRequest(
            id=folder.id,
            filter=new_filter
        ))

        return {
            "success": True,
            "folder_id": folder_id,
            "operation": "add" if add_to_folder else "remove",
            "results": results
        }

    except Exception:
        logger.exception("Cannot update folder %s", folder_id)
        return {
            "success": False,
            "error": "UPDATE_FAILED"
        }


async def get_all_folders(client):
    try:
        result = await client(GetDialogFiltersRequest())
        filters = result.filters if hasattr(result, "filters") else list(result)

        folders = []
        for dialog_filter in filters:
            folder_id = getattr(dialog_filter, "id", None)
            if folder_id is None:
                continue

            title = getattr(dialog_filter, "title", None)
            if hasattr(title, "text"):
                title = title.text

            chat_ids = [
                peer_id
                for peer in getattr(dialog_filter, "include_peers", [])
                if (peer_id := _peer_id(peer)) is not None
            ]

            folders.append({
                "id": folder_id,
                "title": title,
                "chatIds": chat_ids
            })

        return folders

    except Exception:
        logger.exception("Ошибка получения списка папок")
        return []


async def get_chat_folders(client, chat_id):
    folders = []
    try:
        dialogs = await client.get_dialogs()

        folder_ids = set()
        for dialog in dialogs:
            if dialog.id == chat_id and dialog.folder_id is not None:
                folder_ids.add(dialog.folder_id)

        if folder_ids:
            all_folders = await get_all_folders(client)
            for folder in all_folders:
                if folder['id'] in folder_ids:
                    folders.append(folder)
    except Exception as e:
        print(f"❌ Ошибка получения папок для чата {chat_id}: {e}")

    return folders


async def process_update_folders(data, account_configs, run_async_func):
    account_id = data.get('accountId')

    if account_id is None:
        items = data.get('items', [])
        if items and len(items) > 0:
            account_id = items[0].get('tgAccountId')

    if account_id is None:
        account_id = data.get('tgAccountId')

    folder_id = data.get('folderId')
    chat_ids = data.get('chatIds', [])
    add_to_folder = data.get('addOperationFlag', True)

    if not chat_ids:
        items = data.get('items', [])
        for item in items:
            chat_id = item.get('id')
            if chat_id:
                chat_ids.append(chat_id)
        if folder_id is None and items:
            folder_id = items[0].get('folderId')

    if not account_id:
        return {'error': 'Не указан accountId'}

    if not folder_id:
        return {'error': 'Не указан ID папки'}

    if not chat_ids:
        return {'error': 'Не указаны ID чатов'}

    account_id = str(account_id)

    if account_id not in account_configs:
        return {'error': f'Аккаунт {account_id} не инициализирован'}

    from tg_utils import get_client
    client = await get_client(account_id, account_configs)

    result = await update_chat_folders(client, folder_id, chat_ids, add_to_folder)

    return result