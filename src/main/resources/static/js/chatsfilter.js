jQuery(document).ready(function () {
    jQuery('select[multiple]#tgAccounts').multiselect({
        columns: 1,
        search: true,
        selectAll: true,
        texts: {
            placeholder: 'Тг-аккаунты',
            search: 'Тг-аккаунты'
        }
    });

    var action = "/chat/search";

    jQuery("#filter").click(function (e) {
        e.preventDefault();

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

        console.log("Отправляемые данные:", formData);

        jQuery.ajax({
            url: action,
            type: "GET",
            data: formData,
            success: function (response) {
                console.log("Ответ от сервера:", response);
                jQuery(".icontent").remove();
                jQuery(".econtent").html(response);

                var titleOnlyFlag = jQuery("table.list").hasClass("onlychatheaders");
                var messagesCount = Number(jQuery("#messagesCount").val());

                if (messagesCount > 0 && titleOnlyFlag) {
                    jQuery("table.list").removeClass("onlychatheaders")
                }
                if (messagesCount == 0 && !titleOnlyFlag) {
                    jQuery("table.list").addClass("onlychatheaders")
                }
            },
            error: function (xhr, status, error) {
                console.error("Ошибка:", error);
            }
        });
    });
})