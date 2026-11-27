// ===== Справочники с кнопкой «Добавить» =====
(function ($) {
    var dictionaries = {
        university: {url: "/education/universities", select: ".university-select", title: "Новый университет"},
        speciality: {url: "/education/speciality", select: ".speciality-select", title: "Новая специальность"},
        specialization: {url: "/development/specializations", select: ".specialization-select", title: "Новое направление"},
        skill: {url: "/development/skills", select: ".skills-select", title: "Новый навык", multi: true}
    };
    var optionsCache = {};   // key -> актуальный список после первого обновления
    var currentDict = null;
    var $dictTarget = null;

    var skillsMultiselect = {
        columns: 1,
        search: true,
        selectAll: true,
        texts: {placeholder: 'Навыки', search: 'Навыки'}
    };

    // pick = {$el, id}: селект, в котором нужно выбрать только что созданную запись
    function fillOptions($selects, items, multi, pick) {
        $selects.each(function () {
            var $s = $(this);
            var current = $s.val();
            if (multi) {
                current = current || [];
            }
            if (pick && pick.id && pick.$el && pick.$el[0] === this) {
                if (multi) {
                    current = current.concat(String(pick.id));
                } else {
                    current = String(pick.id);
                }
            }
            $s.empty();
            if (!multi) {
                $s.append($("<option>").val("").text("-- не выбрано --"));
            }
            $.each(items, function (_, it) {
                $s.append($("<option>").val(it.id).text(it.name));
            });
            $s.val(current);
            if (multi && $s.data("ms-ready")) {
                $s.multiselect("reload"); // плагин перестраивает список по состоянию <select>
            }
        });
    }

    // новые строки/профили/проекты вызывают это, чтобы получить свежие списки
    window.applyDictionaryCache = function ($scope) {
        $.each(optionsCache, function (key, items) {
            fillOptions($scope.find(dictionaries[key].select), items, dictionaries[key].multi);
        });
    };

    // заполняет и инициализирует мультиселекты навыков в новой панели
    window.initSkillSelects = function ($scope) {
        $scope.find("select.skills-select").each(function () {
            var $s = $(this);
            if (optionsCache.skill) {
                fillOptions($s, optionsCache.skill, true);
            } else {
                $s.html($("#skill-options-template").html());
            }
            $s.multiselect(skillsMultiselect);
            $s.data("ms-ready", true);
        });
    };

    function reloadDictionary(key, newId, newName) {
        var cfg = dictionaries[key];
        return $.ajax({url: cfg.url + "/values", dataType: "json", cache: false})
            .done(function (items) {
                optionsCache[key] = items;
                var id = newId;
                if (!id) {
                    var found = items.filter(function (i) { return i.name === newName; })[0];
                    id = found && found.id;
                }
                fillOptions($(cfg.select), items, cfg.multi, {$el: $dictTarget, id: id});
            });
    }

    $(document).on("click", ".add-dictionary", function () {
        currentDict = $(this).data("dict");
        $dictTarget = $(this).closest(".with-button").find("select").first();
        var $dlg = $("#dictionary-dialog");
        $dlg.find(".dict-title").text(dictionaries[currentDict].title);
        $dlg.find("input").val("");
        Fancybox.show([{src: "#dictionary-dialog", type: "inline"}]);
    });

    $(document).on("click", ".dict-cancel", function () {
        Fancybox.close();
    });

    $(document).on("click", ".dict-create", function () {
        var $dlg = $(this).closest(".dictionary-dialog");
        var code = $.trim($dlg.find(".dict-code").val());
        var name = $.trim($dlg.find(".dict-name").val());
        if (!code || !name) {
            alert("Заполните код и название");
            return;
        }
        var key = currentDict;
        Fancybox.close();

        $.ajax({
            url: dictionaries[key].url + "/add",
            method: "POST",
            data: {code: code, name: name},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken); // объявлены в общем шаблоне
            }
        })
            .done(function (newId) {
                reloadDictionary(key, $.isNumeric(newId) ? newId : null, name);
            })
            .fail(function () { alert("Не удалось создать запись"); });
    });
})(jQuery);

// ===== Профили и проекты =====
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
        }
        $tabs.find("li").removeClass("ui-corner-top").addClass("ui-corner-left");
        $tabs.tabs("option", "active", $tabs.children("ul").children().length - 1);

        initSkillSelects($("#" + id)); // после активации вкладки, чтобы панель была видима
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

        var $spec = $panel.find("select.specialization-select");
        $spec.html($("#specialization-options-template").html());
        applyDictionaryCache($panel); // если списки уже обновляли, берём свежие

        // навыки профиля; область сужена до шапки, чтобы не задеть селекты проектов
        initSkillSelects($panel.children(".profile-head"));

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

// ===== Государство -> города =====
jQuery(function ($) {
    var $state = $("#stateId");
    var $city = $("#cityId");

    function loadCities(selectedId) {
        var stateId = $state.val();
        $city.empty().append('<option value="">-- не выбрано --</option>');

        if (!stateId) {
            $city.prop("disabled", true);
            return;
        }
        $.getJSON($city.data("url"), {stateId: stateId})
            .done(function (items) {
                $.each(items, function (_, it) {
                    $city.append($("<option>").val(it.id).text(it.name));
                });
                if (selectedId) {
                    $city.val(selectedId);
                }
            })
            .always(function () {
                $city.prop("disabled", false);
            });
    }

    $state.on("change", function () {
        loadCities();
    });

    loadCities($city.data("selected")); // для формы редактирования
});

// ===== Правые вкладки и языки =====
jQuery(function ($) {
    $("#right-tabs").tabs();

    var languageTpl = $("#language-template").html();

    function reindexLanguages() {
        $("#languages-body tr").each(function (i) {
            $(this).find("[name]").each(function () {
                this.name = this.name.replace(/languages\[\d+\]/, "languages[" + i + "]");
            });
        });
    }

    function addLanguage(languageId, level) {
        var next = $("#languages-body tr").length;
        var $row = $(languageTpl.replace(/__L__/g, next));
        if (languageId) {
            $row.find("select[name$='.languageId']").val(languageId);
        }
        if (level) {
            $row.find("select[name$='.level']").val(level);
        }
        $("#languages-body").append($row);
    }

    $("#add-language").on("click", function () {
        addLanguage(); // новые строки по кнопке остаются пустыми
    });

    $(document).on("click", ".remove-language", function () {
        $(this).closest("tr").remove();
        reindexLanguages(); // чтобы в списке не было дыр (null-элементов)
    });

    // по умолчанию: русский (id приходит с сервера), родной
    addLanguage($("#tab-languages").data("defaultLanguageId"), "NATIVE");
});

// ===== Образование =====
jQuery(function ($) {
    var educationTpl = $("#education-template").html();

    function addEducation() {
        var next = $("#education-table .education-item").length;
        var $item = $(educationTpl.replace(/__E__/g, next).trim());
        $("#education-table").append($item);
        applyDictionaryCache($item);
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