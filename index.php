<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Справочник по технологиям</title>
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.min.js"></script>
    <style>
        body { font-family: 'Segoe UI', sans-serif; padding: 20px; max-width: 1000px; margin: 0 auto; background: #f8f9fa; }
        .group-accordion { background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; margin-bottom: 20px; }
        .group-accordion .ui-accordion-header {
            background: #e9ecef;
            color: #212529;
            border: none;
            padding: 12px 20px;
            margin: 0;
            font-size: 1.1rem;
            cursor: pointer;
            font-weight: 600;
        }
        .group-accordion .ui-accordion-header:hover { background: #dee2e6; }
        .group-accordion .ui-accordion-content {
            padding: 10px 15px;
            background: #f8f9fa;
            border: none;
        }
        .sub-accordion {
            background: white;
            border-radius: 6px;
            overflow: hidden;
            margin-bottom: 8px;
        }
        .sub-accordion .ui-accordion-header {
            background: #e9ecef;
            color: #0d6efd;
            border: none;
            padding: 10px 16px;
            margin: 0;
            font-size: 1rem;
            cursor: pointer;
            font-weight: 500;
        }
        .sub-accordion .ui-accordion-header:hover { background: #dee2e6; }
        .sub-accordion .ui-accordion-content {
            padding: 10px 15px;
            background: #f8f9fa;
            border: none;
        }
        .question-list { list-style: none; padding: 0; margin: 0; }
        .question-list li { margin-bottom: 6px; }
        .question-list .question-toggle {
            display: block;
            padding: 8px 12px;
            background: white;
            border-radius: 6px;
            cursor: pointer;
            color: #0d6efd;
            font-weight: 500;
            border: 1px solid #dee2e6;
            transition: all 0.2s;
            font-size: 0.95rem;
        }
        .question-list .question-toggle:hover { background: #e9ecef; border-color: #0d6efd; }
        .question-list .question-toggle .badge {
            background: #6c757d;
            color: white;
            padding: 2px 10px;
            border-radius: 20px;
            font-size: 0.7rem;
            margin-left: 10px;
        }
        .question-content { display: none; padding: 15px; background: white; border-radius: 6px; margin-top: 5px; border-left: 4px solid #0d6efd; }
        .question-content .short-content { background: #f1f3f5; padding: 15px; border-radius: 6px; margin-bottom: 15px; border-left: 4px solid #0d6efd; }
        .question-content .long-content { background: #f8f9fa; padding: 15px; border-radius: 6px; border-left: 4px solid #6c757d; }
        .error { color: #dc3545; padding: 20px; text-align: center; font-size: 1.2rem; }
        .debug { background: #fff3cd; padding: 10px; border: 1px solid #ffeeba; border-radius: 4px; margin-bottom: 15px; font-size: 0.9rem; }
    </style>
</head>
<body>

    <h1>📚 Справочник по технологиям</h1>

    <?php
    // Проверка наличия расширения
    if (!extension_loaded('simplexml')) {
        echo '<div class="error">⚠️ Ошибка: Расширение SimpleXML не установлено.</div>';
        exit;
    }

    // --- 1. ЧИТАЕМ GROUPS.XML ---
    $groupsFile = __DIR__ . '/groups.xml';
    if (!file_exists($groupsFile)) {
        echo '<div class="error">⚠️ Файл groups.xml не найден.</div>';
        exit;
    }

    $groupsXml = simplexml_load_file($groupsFile);
    if ($groupsXml === false) {
        echo '<div class="error">⚠️ Ошибка чтения groups.xml.</div>';
        exit;
    }

    // --- 2. СТРОИМ ДЕРЕВО ГРУПП (как вы сказали) ---
    // Верхний уровень = все unique parent (кроме пустых)
    $parents = [];
    $children = [];

    foreach ($groupsXml->group as $group) {
        $name = (string)$group->name;
        $parent = (string)$group->parent;
        
        // Собираем все уникальные parent (это верхний уровень)
        if ($parent !== '') {
            $parents[] = $parent;
        }
        
        // Собираем детей: parent -> [name1, name2, ...]
        if ($parent !== '') {
            if (!isset($children[$parent])) {
                $children[$parent] = [];
            }
            $children[$parent][] = $name;
        }
    }

    // Убираем дубликаты и сортируем
    $parents = array_unique($parents);
    sort($parents);

    foreach ($children as $parent => $childList) {
        $children[$parent] = array_unique($childList);
        sort($children[$parent]);
    }

    // --- 3. ЧИТАЕМ ВСЕ ФАЙЛЫ В ПАПКЕ DATA ---
    $dataDir = __DIR__ . '/data';
    if (!is_dir($dataDir)) {
        echo '<div class="error">⚠️ Папка data не найдена.</div>';
        exit;
    }

    $allFiles = scandir($dataDir);
    $files = array_filter($allFiles, function($file) use ($dataDir) {
        return is_file($dataDir . '/' . $file) && $file !== '.' && $file !== '..';
    });

    if (empty($files)) {
        echo '<div class="error">⚠️ В папке data нет файлов.</div>';
        exit;
    }

    $questions = [];

    foreach ($files as $file) {
        $filePath = $dataDir . '/' . $file;
        $content = file_get_contents($filePath);
        if (empty($content)) continue;
        
        preg_match_all('/<question>(.*?)<\/question>/s', $content, $matches);
        if (empty($matches[1])) continue;

        foreach ($matches[1] as $questionXml) {
            if (!preg_match('/<title>(.*?)<\/title>/s', $questionXml, $titleMatch)) continue;
            
            preg_match('/<short>(.*?)<\/short>/s', $questionXml, $shortMatch);
            $short = trim($shortMatch[1] ?? '');
            
            preg_match('/<long>(.*?)<\/long>/s', $questionXml, $longMatch);
            $long = trim($longMatch[1] ?? '');
            
            preg_match_all('/<group>(.*?)<\/group>/s', $questionXml, $groupsMatch);
            
            $title = htmlspecialchars(trim($titleMatch[1] ?? 'Без названия'));
            $questionGroups = array_map('trim', $groupsMatch[1] ?? []);

            if (empty($short) && empty($long)) continue;
            if (empty($questionGroups)) $questionGroups = ['Общее'];

            foreach ($questionGroups as $group) {
                if (!isset($questions[$group])) $questions[$group] = [];
                $questions[$group][] = [
                    'title' => $title,
                    'short' => $short,
                    'long' => $long
                ];
            }
        }
    }

    // --- 4. ВЫВОДИМ АККОРДЕОН ---
    ?>

    <div id="main-accordion" class="group-accordion">
        <?php foreach ($parents as $parent): ?>
            <?php 
            $escapedParent = htmlspecialchars($parent);
            $parentId = 'parent-' . md5($parent);
            ?>
            
            <h3><?= $escapedParent ?></h3>
            <div>
                <?php if (isset($children[$parent]) && !empty($children[$parent])): ?>
                    <div id="sub-accordion-<?= $parentId ?>" class="sub-accordion">
                        <?php foreach ($children[$parent] as $child): ?>
                            <?php 
                            $escapedChild = htmlspecialchars($child);
                            $childId = 'child-' . md5($child);
                            ?>
                            
                            <h3><?= $escapedChild ?></h3>
                            <div>
                                <?php if (isset($questions[$child]) && !empty($questions[$child])): ?>
                                    <ul class="question-list">
                                        <?php foreach ($questions[$child] as $index => $question): ?>
                                            <?php 
                                            $qId = 'q-' . md5($child . $question['title'] . $index);
                                            ?>
                                            <li>
                                                <div class="question-toggle" data-target="<?= $qId ?>">
                                                    <?= $question['title'] ?>
                                                    <span class="badge">развернуть</span>
                                                </div>
                                                <div id="<?= $qId ?>" class="question-content">
                                                    <?php if (!empty($question['short'])): ?>
                                                        <div class="short-content">
                                                            <strong>Кратко:</strong> <?= $question['short'] ?>
                                                        </div>
                                                    <?php endif; ?>
                                                    <?php if (!empty($question['long'])): ?>
                                                        <div class="long-content">
                                                            <strong>Подробно:</strong><br><?= nl2br($question['long']) ?>
                                                        </div>
                                                    <?php endif; ?>
                                                </div>
                                            </li>
                                        <?php endforeach; ?>
                                    </ul>
                                <?php else: ?>
                                    <p style="color: #6c757d; padding: 10px;">Нет вопросов в этой группе</p>
                                <?php endif; ?>
                            </div>
                            
                        <?php endforeach; ?>
                    </div>
                <?php endif; ?>
                
                <?php if (isset($questions[$parent]) && !empty($questions[$parent])): ?>
                    <ul class="question-list">
                        <?php foreach ($questions[$parent] as $index => $question): ?>
                            <?php 
                            $qId = 'q-parent-' . md5($parent . $question['title'] . $index);
                            ?>
                            <li>
                                <div class="question-toggle" data-target="<?= $qId ?>">
                                    <?= $question['title'] ?>
                                    <span class="badge">развернуть</span>
                                </div>
                                <div id="<?= $qId ?>" class="question-content">
                                    <?php if (!empty($question['short'])): ?>
                                        <div class="short-content">
                                            <strong>Кратко:</strong> <?= $question['short'] ?>
                                        </div>
                                    <?php endif; ?>
                                    <?php if (!empty($question['long'])): ?>
                                        <div class="long-content">
                                            <strong>Подробно:</strong><br><?= nl2br($question['long']) ?>
                                        </div>
                                    <?php endif; ?>
                                </div>
                            </li>
                        <?php endforeach; ?>
                    </ul>
                <?php endif; ?>
            </div>
            
        <?php endforeach; ?>
    </div>

    <script>
        $(function() {
            // Инициализируем главный аккордеон
            $("#main-accordion").accordion({
                collapsible: true,
                active: false,
                heightStyle: "content"
            });

            // Инициализируем все вложенные аккордеоны
            $(".sub-accordion").accordion({
                collapsible: true,
                active: false,
                heightStyle: "content"
            });

            // Обработчик клика по вопросу (toggle)
            $(".question-toggle").on("click", function() {
                var targetId = $(this).data("target");
                var content = $("#" + targetId);
                var badge = $(this).find(".badge");

                content.slideToggle(200);
                badge.text(content.is(":visible") ? "свернуть" : "развернуть");
            });
        });
    </script>

</body>
</html>
