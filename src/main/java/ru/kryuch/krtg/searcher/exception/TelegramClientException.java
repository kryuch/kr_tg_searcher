package ru.kryuch.krtg.searcher.exception;

public class TelegramClientException extends RuntimeException {

    public TelegramClientException() {
        super();
    }

    public TelegramClientException(String message) {
        super(message);
    }
}
