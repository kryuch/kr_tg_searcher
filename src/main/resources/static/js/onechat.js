$(document).ready(function () {
    $('#sendMessageBtn').click(function (e) {
        e.preventDefault();

        var back = $('#back').val();
        var message = $('#message').val().trim();

        if (!back) {
            alert('ID чата не найден');
            return;
        }

        if (!message) {
            alert('Введите текст сообщения');
            return;
        }

        var form = $('<form>', {
            action: '/chat/send',
            method: 'POST'
        });

        $('<input>', {
            type: 'hidden',
            name: 'back',
            value: back
        }).appendTo(form);

        $('<input>', {
            type: 'hidden',
            name: 'message',
            value: message
        }).appendTo(form);

        // Пустой chatIds, чтобы контроллер не падал
        $('<input>', {
            type: 'hidden',
            name: 'chatIds',
            value: ''
        }).appendTo(form);

        $('body').append(form);
        form.submit();
    });
});