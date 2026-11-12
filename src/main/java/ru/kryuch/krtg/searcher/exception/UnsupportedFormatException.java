package ru.kryuch.krtg.searcher.exception;

public class UnsupportedFormatException extends RuntimeException {
    public UnsupportedFormatException(String format) {
        super(String.format("Unsupported format: %s", format));
    }
}
