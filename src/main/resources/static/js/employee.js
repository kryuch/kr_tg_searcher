jQuery(function ($) {
    var $profileTabs = $("#profile-tabs").tabs();
    var profileTpl = $("#profile-template").html();
    var projectTpl = $("#project-template").html();
    var profileCounter = 0;

    function addProject($profilePanel) {
        var p = $profilePanel.data("index");
        var r = $profilePanel.data("nextProject");
        $profilePanel.data("nextProject", r + 1);

        var id = "project-" + p + "-" + r;
        var $tabs = $profilePanel.find(".projects-tabs");

        $tabs.children("ul").append(
            '<li><a href="#' + id + '" data-default="Проект ' + (r + 1) + '">Проект ' + (r + 1) + '</a></li>'
        );
        $tabs.append(
            '<div class="project-panel" id="' + id + '">'
            + projectTpl.replace(/__P__/g, p).replace(/__R__/g, r)
            + '</div>'
        );

        if ($tabs.hasClass("ui-tabs")) {
            $tabs.tabs("refresh");
        } else {
            $tabs.tabs().addClass("ui-tabs-vertical ui-helper-clearfix");
            $tabs.find("li").removeClass("ui-corner-top").addClass("ui-corner-left");
        }
        $tabs.find("li").removeClass("ui-corner-top").addClass("ui-corner-left");
        $tabs.tabs("option", "active", $tabs.children("ul").children().length - 1);
    }

    function addProfile() {
        var p = profileCounter++;
        var id = "profile-tab-" + p;

        $profileTabs.children("ul").append(
            '<li><a href="#' + id + '" data-default="Профиль ' + (p + 1) + '">Профиль ' + (p + 1) + '</a></li>'
        );
        var $panel = $('<div class="profile-panel" id="' + id + '"></div>')
            .html(profileTpl.replace(/__P__/g, p))
            .data("index", p)
            .data("nextProject", 0);
        $profileTabs.append($panel);
        $profileTabs.tabs("refresh");
        $profileTabs.tabs("option", "active", $profileTabs.children("ul").children().length - 1);

        var $grades = $panel.find("select.grades-select");
        $grades.html($("#grade-options-template").html()).multiselect({
            columns: 1,
            search: true,
            selectAll: true,
            texts: {
                placeholder: 'Грейды',
                search: 'Грейды'
            }
        });

        addProject($panel); // по умолчанию один проект
    }

// подпись вкладки = введённое название (или "Профиль N"/"Проект N" по умолчанию)
    $(document).on("input", "[data-tab-title]", function () {
        var panelId = $(this).closest(".ui-tabs-panel").attr("id");
        var $a = $('a[href="#' + panelId + '"]');
        $a.text($.trim($(this).val()) || $a.data("default"));
    });

    $("#add-profile").on("click", addProfile);

    $(document).on("click", ".add-project", function () {
        addProject($(this).closest(".profile-panel"));
    });

    addProfile(); // по умолчанию один профиль
});

jQuery(function ($) {
    var $state = $("#state");
    var $city = $("#city");

    function loadCities(selected) {
        var stateId = $state.find("option:selected").data("id");
        $city.empty().append('<option value="">-- не выбрано --</option>');

        if (!stateId) {
            $city.prop("disabled", true);
            return;
        }
        $.getJSON($city.data("url"), {stateId: stateId})
            .done(function (names) {
                $.each(names, function (_, name) {
                    $city.append($("<option>").val(name).text(name));
                });
                if (selected) {
                    $city.val(selected);
                }
            })
            .always(function () {
                $city.prop("disabled", false);
            });
    }

    $state.on("change", function () {
        loadCities();
    });

    loadCities($city.data("selected")); // понадобится для формы редактирования

    $("#right-tabs").tabs();

    var languageTpl = $("#language-template").html();

    function reindexLanguages() {
        $("#languages-body tr").each(function (i) {
            $(this).find("[name]").each(function () {
                this.name = this.name.replace(/languages\[\d+\]/, "languages[" + i + "]");
            });
        });
    }

    $(document).on("click", ".remove-language", function () {
        $(this).closest("tr").remove();
        reindexLanguages(); // чтобы в списке не было дыр (null-элементов)
    });

    function addLanguage(code, level) {
        var next = $("#languages-body tr").length;
        var $row = $(languageTpl.replace(/__L__/g, next));
        if (code) {
            $row.find("select[name$='.language']").val(code);
        }
        if (level) {
            $row.find("select[name$='.level']").val(level);
        }
        $("#languages-body").append($row);
    }

    $("#add-language").on("click", function () {
        addLanguage(); // новые строки по кнопке остаются пустыми
    });

    addLanguage("RU", "NATIVE"); // по умолчанию: русский, родной

    var educationTpl = $("#education-template").html();

    function addEducation() {
        var next = $("#education-table .education-item").length;
        $("#education-table").append(educationTpl.replace(/__E__/g, next));
    }

    function reindexEducations() {
        $("#education-table .education-item").each(function (i) {
            $(this).find("[name]").each(function () {
                this.name = this.name.replace(/educations\[\d+\]/, "educations[" + i + "]");
            });
        });
    }

    $("#add-education").on("click", addEducation);

    $(document).on("click", ".remove-education", function () {
        $(this).closest(".education-item").remove();
        reindexEducations();
    });
});