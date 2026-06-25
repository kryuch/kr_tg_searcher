package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class MessagesHistory {

    private List<Message> values = new ArrayList<>();

    private ChatInfo chatInfo;

    private HashMap<LocalDate, List <Message> > groupedValues;

    public void add(Message message) {
        values.add(message);

        LocalDate date = message.getDateTime().toLocalDate();
        if (Objects.isNull(groupedValues)) groupedValues = new LinkedHashMap<>();
        groupedValues.computeIfAbsent(date, k -> new ArrayList<>()).add(message);
    }

    public Map<LocalDate, List<Message>> getSortedGroupedValues() {
        return groupedValues.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        Map::putAll);
    }

    public int size() {
        return values.size();
    }
}
