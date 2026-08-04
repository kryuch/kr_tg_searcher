$(document).ready(function() {

    // ============================================================
    // 0. ПОЛУЧАЕМ CSRF-ТОКЕН
    // ============================================================
    var csrfToken = $('meta[name="_csrf"]').attr('content');
    var csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    // ============================================================
    // 0.1. МОДИФИКАЦИЯ СТРАНИЦЫ
    // ============================================================

    // 1) Скрываем весь tr, в котором находится кнопка "Сохранить"
    $('form[action="/tg/add"] button.save[type="submit"]').closest('tr').hide();

    // 2) Делаем все текстовые поля внутри формы /tg/add недоступными для редактирования
    $('form[action="/tg/add"] input[type="text"]').each(function() {
        $(this).prop('readonly', true);
        $(this).css('background-color', '#f0f0f0');
    });

    // 3) Скрываем кнопку "Подтвердить код" (она будет показана после успешной отправки кода)
    $('#verifyCode').hide();

    // 4) Делаем поля ввода кода и пароля активными
    $('#code, #password').prop('disabled', false);

    // 5) ПЕРЕМЕЩАЕМ все строки из таблицы authform в таблицу внутри формы /tg/add
    var $targetTable = $('form[action="/tg/add"] table.list');
    var $sourceRows = $('.authform table.list tr');

    var $lastRow = $targetTable.find('tr:last-child');

    if ($lastRow.find('button').length > 0) {
        $sourceRows.each(function() {
            $(this).insertBefore($lastRow);
        });
    } else {
        $targetTable.append($sourceRows);
    }

    // ============================================================
    // 1. ОТПРАВКА ЗАПРОСА НА ПОЛУЧЕНИЕ КОДА
    // ============================================================
    $("#receiveCode").click(function(e) {
        e.preventDefault();

        var tgAccountId = $("#id").val().trim();

        if (!tgAccountId) {
            showMessage('Укажите ID аккаунта', 'error');
            return;
        }

        var $btn = $(this);
        $btn.prop('disabled', true).text('Отправка...');
        hideMessage();

        $.ajax({
            url: '/tg/auth/' + tgAccountId + '/receive',
            type: 'POST',
            headers: csrfToken ? { [csrfHeader]: csrfToken } : {},
            dataType: 'json',
            success: function(response) {
                console.log('✅ Ответ от сервера:', response);

                if (response.success === true) {
                    // Проверяем, авторизован ли уже аккаунт
                    if (response.authorised === true) {
                        showMessage('✅ Аккаунт уже авторизован!', 'success');
                        // Редирект на список аккаунтов
                        setTimeout(function() {
                            window.location.href = '/tg/list';
                        }, 1000);
                    } else {
                        showMessage('Код отправлен на ваш номер телефона! Проверьте Telegram.', 'success');
                        $('#verifyCode').show();
                        $('#code').prop('disabled', false).focus();
                        $('#password').prop('disabled', false);
                    }
                } else {
                    var errorMsg = response.error || 'Ошибка при отправке кода';
                    showMessage(errorMsg, 'error');
                }
            },
            error: function(xhr, status, error) {
                var errorMsg = 'Ошибка при отправке кода: ';

                if (xhr.responseJSON) {
                    if (xhr.responseJSON.message) {
                        errorMsg += xhr.responseJSON.message;
                    } else if (xhr.responseJSON.error) {
                        errorMsg += xhr.responseJSON.error;
                    } else {
                        errorMsg += JSON.stringify(xhr.responseJSON);
                    }
                } else {
                    errorMsg += error;
                }

                showMessage(errorMsg, 'error');
                console.error('❌ Ошибка:', error);
            },
            complete: function() {
                $btn.prop('disabled', false).text('Отправить код');
            }
        });
    });

    // ============================================================
    // 2. ПОДТВЕРЖДЕНИЕ КОДА
    // ============================================================
    $("#verifyCode").click(function(e) {
        e.preventDefault();

        var tgAccountId = $("#id").val().trim();
        var code = $("#code").val().trim();
        var password = $("#password").val().trim();

        if (!tgAccountId) {
            showMessage('Укажите ID аккаунта', 'error');
            return;
        }

        if (!code) {
            showMessage('Введите код из Telegram', 'error');
            return;
        }

        var $btn = $(this);
        $btn.prop('disabled', true).text('Проверка...');
        hideMessage();

        var requestData = {
            tgAccountId: parseInt(tgAccountId),
            code: code
        };

        if (password) {
            requestData.password = password;
        }

        $.ajax({
            url: '/tg/auth/verify',
            type: 'POST',
            contentType: 'application/json',
            headers: csrfToken ? { [csrfHeader]: csrfToken } : {},
            data: JSON.stringify(requestData),
            dataType: 'json',
            success: function(response) {
                console.log('✅ Ответ верификации:', response);

                if (response.success === true) {
                    showMessage('✅ Аккаунт успешно авторизован!', 'success');
                    console.log('✅ Авторизация успешна:', response);

                    setTimeout(function() {
                        window.location.href = '/tg/list';
                    }, 2000);
                } else {
                    var errorMsg = response.error || response.message || 'Ошибка при проверке кода';
                    showMessage(errorMsg, 'error');
                }
            },
            error: function(xhr, status, error) {
                var errorMsg = 'Ошибка при проверке кода: ';

                if (xhr.responseJSON) {
                    if (xhr.responseJSON.message) {
                        errorMsg += xhr.responseJSON.message;
                    } else if (xhr.responseJSON.error) {
                        errorMsg += xhr.responseJSON.error;
                    } else {
                        errorMsg += JSON.stringify(xhr.responseJSON);
                    }
                } else {
                    errorMsg += error;
                }

                showMessage(errorMsg, 'error');
                console.error('❌ Ошибка:', error);
            },
            complete: function() {
                $btn.prop('disabled', false).text('Подтвердить код');
            }
        });
    });

    // ============================================================
    // 3. ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ
    // ============================================================
    function showMessage(text, type) {
        var $msg = $('#authMessage');

        if (!$msg.length) {
            console.error('❌ Элемент #authMessage не найден');
            return;
        }

        $msg.text(text)
            .removeClass('success error')
            .addClass(type)
            .css('display', 'block');

        if (type === 'success') {
            $msg.css({
                'background-color': '#d4edda',
                'color': '#155724',
                'border': '1px solid #c3e6cb'
            });
        } else if (type === 'error') {
            $msg.css({
                'background-color': '#f8d7da',
                'color': '#721c24',
                'border': '1px solid #f5c6cb'
            });
        }
    }

    function hideMessage() {
        var $msg = $('#authMessage');
        if ($msg.length) {
            $msg.css('display', 'none').text('');
        }
    }

    $(document).on('click', '#authMessage', function() {
        hideMessage();
    });

});