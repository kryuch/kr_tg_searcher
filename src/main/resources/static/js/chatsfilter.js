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
// Функции для показа/скрытия спиннера
// ============================================================

function showLoading(button) {
    // Сохраняем оригинальный текст
    var $btn = $(button);
    if (!$btn.data('original-text')) {
        $btn.data('original-text', $btn.text());
    }

    // Отключаем кнопку и показываем спиннер
    $btn.prop('disabled', true);
    $btn.html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Загрузка...');

    // Добавляем overlay на всю страницу (опционально)
    if (!$('#loading-overlay').length) {
        $('body').append('<div id="loading-overlay" style="position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.3);z-index:9999;display:none;"></div>');
    }
    $('#loading-overlay').show();
}

function hideLoading(button) {
    var $btn = $(button);
    var originalText = $btn.data('original-text') || $btn.text();
    $btn.prop('disabled', false);
    $btn.text(originalText);
    $btn.removeData('original-text');

    $('#loading-overlay').hide();
}

// ============================================================
// Функции для работы с фильтрами (без изменений)
// ============================================================

function applyFilter() {
    var formData = {
        term: document.getElementById('term')?.value || '',
        maxFoundCount: document.getElementById('maxFoundCount')?.value || '',
        minDiffDaysCount: document.getElementById('minDiffDaysCount')?.value || '',
        excludeStatusFlag: document.getElementById('excludeStatusFlag')?.checked || false,
        excludeBotFlag: document.getElementById('excludeBotFlag')?.checked || false,
        excludeGroupFlag: document.getElementById('excludeGroupFlag')?.checked || false
    };

    var params = new URLSearchParams();
    for (var key in formData) {
        if (formData[key] !== '' && formData[key] !== false) {
            params.append(key, formData[key]);
        }
    }

    fetch('/chat/search?' + params.toString())
        .then(response => response.text())
        .then(html => {
            document.querySelector('.econtent').innerHTML = html;
        })
        .catch(err => console.error('Ошибка фильтрации:', err));
}

// ============================================================
// Сохранение параметров поиска в cookies
// ============================================================

function saveSearchParamsToCookies(params) {
    setCookie('searchParams', params, 30);
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
// Функция formSubmit с поддержкой спиннера
// ============================================================

function formSubmit(url, func, button) {
    var selectedChats = getSelectedItems();
    if (selectedChats == null) return;

    // Показываем спиннер на кнопке
    if (button) {
        showLoading(button);
    }

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

    // Скрываем спиннер через некоторое время (форма отправлена, но ответ ещё не пришёл)
    // Спиннер скроется после загрузки страницы
    setTimeout(function() {
        if (button) {
            hideLoading(button);
        }
    }, 5000);
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
        formSubmit('/chat/toFolder', null, this);
    });

    $("#fromFolder").click(function (e) {
        e.preventDefault();
        formSubmit('/chat/fromFolder', null, this);
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
        }, this);
    });

    $("#export").click(function (e) {
        e.preventDefault();

        formSubmit('/chat/export', function (form) {
            var $btn = $(this);
            var originalText = $btn.text();
            $btn.text('Экспорт...').prop('disabled', true);
            $btn.text(originalText).prop('disabled', false);
        }, this);
    });

    // ============================================================
    // 3. ОБРАБОТЧИК ФИЛЬТРА (со спиннером)
    // ============================================================

    var action = "/chat/search";

    jQuery("#filter").click(function (e) {
        e.preventDefault();

        // Показываем спиннер на кнопке
        var $btn = $(this);
        showLoading($btn);

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
            hideLoading($btn);
            return false;
        }

        // Сохраняем параметры в cookies
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

                // Скрываем спиннер после успешного ответа
                hideLoading($btn);
            },
            error: function (xhr, status, error) {
                console.error("❌ Ошибка:", error);
                alert("Ошибка при выполнении запроса: " + error);
                hideLoading($btn);
            }
        });
    });

    // ============================================================
    // 4. ВОССТАНОВЛЕНИЕ СОСТОЯНИЯ ПОСЛЕ SUBMIT (для formSubmit)
    // ============================================================

    // Если страница перезагрузилась после отправки формы,
    // убираем все спиннеры
    $('button').each(function() {
        var $btn = $(this);
        if ($btn.data('original-text')) {
            hideLoading($btn);
        }
    });

    console.log("✅ Страница загружена, параметры восстановлены из cookies");
});