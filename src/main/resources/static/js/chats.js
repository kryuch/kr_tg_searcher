function showMessage(element, flag) {
    console.log(flag);
    debugger;
    var message = jQuery(element).parents(".message");
    var shortElement = message.find(".description-short");
    var longElement = message.find(".description-long");
    if (flag) {
        shortElement.hide();
        longElement.show();
    }
    else {
        shortElement.show();
        longElement.hide();
    }
}

function loadMissingAvatars() {
    console.log("🔍 Проверка аватаров...");

    // Собираем все строки с аватарами
    var rows = document.querySelectorAll('tr[data-chat-id]');
    var missingAvatars = [];
    var avatarMap = new Map(); // Для быстрого поиска элементов по ключу

    rows.forEach(function(row) {
        var avatarDiv = row.querySelector('.chat-avatar');
        if (!avatarDiv) return;

        var img = avatarDiv.querySelector('img');
        // Если аватара нет (нет img или он не загружен)
        if (!img) {
            var userId = avatarDiv.getAttribute('data-user-id');
            var tgAccountId = avatarDiv.getAttribute('data-tg-id');

            if (userId && tgAccountId) {
                var key = userId + '_' + tgAccountId;
                if (!avatarMap.has(key)) {
                    avatarMap.set(key, []);
                }
                avatarMap.get(key).push({
                    element: avatarDiv,
                    userId: userId,
                    tgAccountId: tgAccountId
                });
            }
        }
    });

    // Если нет аватаров для загрузки
    if (avatarMap.size === 0) {
        console.log("✅ Все аватары уже загружены");
        return;
    }

    console.log("📥 Найдено аватаров для загрузки:", avatarMap.size);

    // Формируем запрос
    var requestData = [];
    avatarMap.forEach(function(items) {
        var first = items[0];
        requestData.push({
            userId: parseInt(first.userId),
            tgAccountId: parseInt(first.tgAccountId)
        });
    });

    console.log("📤 Отправка запроса на /chat/status/avatars");

    // Отправляем запрос
    fetch('/chat/status/avatars', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData)
    })
        .then(function(response) {
            if (!response.ok) {
                throw new Error("HTTP " + response.status);
            }
            return response.json();
        })
        .then(function(data) {
            console.log("📥 Получено аватаров:", data.length);

            // Обновляем аватары на странице
            data.forEach(function(avatar) {
                if (!avatar.value) return; // Если аватар пустой

                var userId = avatar.key.userId;
                var tgAccountId = avatar.key.tgAccountId;
                var key = userId + '_' + tgAccountId;

                var items = avatarMap.get(key);
                if (!items) return;

                items.forEach(function(item) {
                    var avatarDiv = item.element;
                    // Удаляем старый placeholder если был
                    var placeholder = avatarDiv.querySelector('div');
                    if (placeholder) {
                        placeholder.remove();
                    }
                    // Создаем и добавляем img
                    var img = document.createElement('img');
                    img.src = 'data:image/jpeg;base64,' + avatar.value;
                    img.style.cssText = 'width:40px; height:40px; border-radius:50%; object-fit:cover;';
                    avatarDiv.appendChild(img);
                });
            });

            console.log("✅ Аватары обновлены");
        })
        .catch(function(error) {
            console.error("❌ Ошибка загрузки аватаров:", error);
        });
}
