// ============================================================
// Функции для работы с cookies
// ============================================================

function setCookie(name, value, days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + encodeURIComponent(JSON.stringify(value)) + expires + "; path=/";
}

function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) === 0) {
            try {
                return JSON.parse(decodeURIComponent(c.substring(nameEQ.length, c.length)));
            } catch (e) {
                return null;
            }
        }
    }
    return null;
}

function deleteCookie(name) {
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
}

// ============================================================
// Сохранение параметров поиска в cookies
// ============================================================

function saveSearchParamsToCookies(params) {
    setCookie('searchParams', params, 30); // храним 30 дней
}

function loadSearchParamsFromCookies() {
    return getCookie('searchParams');
}

function clearSearchParamsCookies() {
    deleteCookie('searchParams');
}

// ============================================================
// Восстановление полей из cookies
// ============================================================

function restoreSearchParamsFromCookies() {
    var params = loadSearchParamsFromCookies();
    if (!params) return;

    console.log("🔄 Восстановление параметров из cookies:", params);

    if (params.term) jQuery("#term").val(params.term);
    if (params.maxFoundCount) jQuery("#maxFoundCount").val(params.maxFoundCount);
    if (params.minDiffDaysCount) jQuery("#minDiffDaysCount").val(params.minDiffDaysCount);
    if (params.lastMessage) jQuery("#lastMessage").val(params.lastMessage);
    if (params.messagesCount) jQuery("#messagesCount").val(params.messagesCount);

    if (params.excludeStatusFlag !== undefined) {
        jQuery("#excludeStatusFlag").prop('checked', params.excludeStatusFlag === true || params.excludeStatusFlag === 'true');
    }

    if (params.botType) jQuery("#botType").val(params.botType);
    if (params.groupType) jQuery("#groupType").val(params.groupType);

    if (params.tgAccountIds && params.tgAccountIds.length > 0) {
        jQuery("#tgAccounts").val(params.tgAccountIds);
        // Обновляем отображение мультиселекта
        jQuery("#tgAccounts").multiselect('refresh');
    }
}

// ============================================================
// Функция getSelectedItems (без изменений)
// ============================================================

function getSelectedItems() {
    var selectedChats = [];

    $('input[name="check[]"]:checked').each(function () {
        var chatId = $(this).data('chat-id');
        if (chatId) {
            selectedChats.push(chatId);
        }
    });

    if (selectedChats.length === 0) {
        alert('Выберите хотя бы один чат');
        return null;
    }

    return selectedChats;
}

// ============================================================
// Функция formSubmit (без изменений)
// ============================================================

function formSubmit(url, func) {
    var selectedChats = getSelectedItems();
    if (selectedChats == null) return;

    var form = $('<form>', {
        action: url,
        method: 'POST'
    });

    $('<input>', {
        type: 'hidden',
        name: '_csrf',
        value: csrfToken
    }).appendTo(form);

    selectedChats.forEach(function (chatId) {
        $('<input>', {
            type: 'hidden',
            name: 'chatIds',
            value: chatId
        }).appendTo(form);
    });

    if (typeof func === 'function') {
        func(form);
    }

    $('body').append(form);
    form.submit();
}

// ============================================================
// ОСНОВНОЙ КОД
// ============================================================

jQuery(document).ready(function () {

    // ============================================================
    // 1. ВОССТАНОВЛЕНИЕ ПАРАМЕТРОВ ИЗ COOKIES ПРИ ЗАГРУЗКЕ
    // ============================================================
    restoreSearchParamsFromCookies();

    // ============================================================
    // 2. ОБРАБОТЧИКИ КНОПОК
    // ============================================================

    $("#checkAll").click(function (e) {
        e.preventDefault();
        $('.icontent table.list input[type="checkbox"]').prop('checked', true);
    });

    $("#uncheckAll").click(function (e) {
        e.preventDefault();
        $('.icontent table.list input[type="checkbox"]').prop('checked', false);
    });

    $("#toFolder").click(function (e) {
        e.preventDefault();
        formSubmit('/chat/toFolder');
    });

    $("#fromFolder").click(function (e) {
        e.preventDefault();
        formSubmit('/chat/fromFolder');
    });

    $("#sendMessage").click(function (e) {
        e.preventDefault();

        formSubmit('/chat/send', function (form) {
            var message = $("#message").val().trim();

            if (!message) {
                alert('Введите текст сообщения');
                form.remove();
                return;
            }

            $('<input>', {
                type: 'hidden',
                name: 'message',
                value: message
            }).appendTo(form);
        });
    });

    $("#export").click(function (e) {
        e.preventDefault();

        formSubmit('/chat/export', function (form) {
            var $btn = $(this);
            var originalText = $btn.text();
            $btn.text('Экспорт...').prop('disabled', true);
            $btn.text(originalText).prop('disabled', false);
        });
    });

    var action = "/chat/search";

    jQuery("#filter").click(function (e) {
        e.preventDefault();

        // Собираем данные формы
        var formData = {
            term: jQuery("#term").val(),
            maxFoundCount: jQuery("#maxFoundCount").val(),
            minDiffDaysCount: jQuery("#minDiffDaysCount").val(),
            excludeStatusFlag: jQuery("#excludeStatusFlag").is(":checked"),
            botType: jQuery("#botType").val(),
            lastMessage: jQuery("#lastMessage").val(),
            groupType: jQuery("#groupType").val(),
            messagesCount: jQuery("#messagesCount").val(),
            tgAccountIds: jQuery("#tgAccounts").val()
        };

        // Проверка выбора ТГ-аккаунтов
        if (jQuery("#tgAccounts").val().length == 0) {
            alert("Необходимо выбрать ТГ-аккаунты");
            return false;
        }

        // СОХРАНЯЕМ ПАРАМЕТРЫ В COOKIES
        saveSearchParamsToCookies(formData);
        console.log("💾 Параметры сохранены в cookies:", formData);

        console.log("📤 Отправляемые данные:", formData);

        // AJAX запрос
        jQuery.ajax({
            url: action,
            type: "GET",
            data: formData,
            success: function (response) {
                console.log("📥 Ответ от сервера:", response);

                // Обновляем содержимое
                jQuery(".icontent").remove();
                jQuery(".econtent").html(response);

                var titleOnlyFlag = jQuery("table.list").hasClass("onlychatheaders");
                var messagesCount = Number(jQuery("#messagesCount").val());

                if (messagesCount > 0 && titleOnlyFlag) {
                    jQuery("table.list").removeClass("onlychatheaders");
                }
                if (messagesCount == 0 && !titleOnlyFlag) {
                    jQuery("table.list").addClass("onlychatheaders");
                }

                // Восстанавливаем состояние чекбоксов после обновления
                // (если нужно — можно добавить логику)
            },
            error: function (xhr, status, error) {
                console.error("❌ Ошибка:", error);
            }
        });
    });

    // ============================================================
    // 5. ОЧИСТКА COOKIES ПРИ ВЫХОДЕ (опционально)
    // ============================================================

    // Если есть кнопка "Сбросить фильтры", можно добавить:
    // $("#clearFilters").click(function(e) {
    //     e.preventDefault();
    //     clearSearchParamsCookies();
    //     location.reload();
    // });

    console.log("✅ Страница загружена, параметры восстановлены из cookies");
});