package ru.kryuch.krtg.searcher.projection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatKeyProjectionImpl implements ChatKeyProjection {

    private final Long userId;
    private final Integer tgAccountId;

    public Long getUserId() {
        return userId;
    }

    public Integer getTgAccountId() {
        return tgAccountId;
    }
}