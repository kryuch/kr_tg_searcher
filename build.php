#!/usr/bin/env php
<?php

/**
 * Скрипт для сборки групп из всех файлов в папке data.
 * Создаёт groups.xml в корне проекта с форматированием.
 */

$dataDir = __DIR__ . '/data';
$outputFile = __DIR__ . '/groups.xml';

if (!is_dir($dataDir)) {
    fwrite(STDERR, "❌ Папка data не найдена.\n");
    exit(1);
}

$files = glob($dataDir . '/*');
if (empty($files)) {
    fwrite(STDERR, "❌ В папке data нет файлов.\n");
    exit(1);
}

$allGroups = [];

foreach ($files as $file) {
    if (basename($file)[0] === '.') continue;
    $content = file_get_contents($file);
    preg_match_all('/<question>(.*?)<\/question>/s', $content, $matches);
    if (empty($matches[1])) continue;
    foreach ($matches[1] as $questionXml) {
        preg_match_all('/<group>(.*?)<\/group>/s', $questionXml, $groupsMatch);
        foreach ($groupsMatch[1] as $group) {
            $group = trim($group);
            if (!empty($group)) {
                $allGroups[$group] = true;
            }
        }
    }
}

if (empty($allGroups)) {
    fwrite(STDERR, "⚠️ Группы не найдены.\n");
    exit(1);
}

ksort($allGroups);

// Создаём XML с форматированием через DOMDocument
$dom = new DOMDocument('1.0', 'UTF-8');
$dom->formatOutput = true;

$groupsNode = $dom->createElement('groups');
$dom->appendChild($groupsNode);

foreach (array_keys($allGroups) as $groupName) {
    $groupNode = $dom->createElement('group');
    $groupsNode->appendChild($groupNode);

    $nameNode = $dom->createElement('name', htmlspecialchars($groupName));
    $groupNode->appendChild($nameNode);

    $parentNode = $dom->createElement('parent');
    $parentNode->appendChild($dom->createTextNode(''));
    $groupNode->appendChild($parentNode);
}

$result = $dom->save($outputFile);

if ($result !== false) {
    $count = count($allGroups);
    fwrite(STDERR, "✅ Файл groups.xml успешно создан. Найдено групп: {$count}.\n");
    exit(0);
} else {
    fwrite(STDERR, "❌ Не удалось сохранить файл groups.xml.\n");
    exit(1);
}
