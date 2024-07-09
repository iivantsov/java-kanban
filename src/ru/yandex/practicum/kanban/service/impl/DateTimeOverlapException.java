package ru.yandex.practicum.kanban.service.impl;

public class DateTimeOverlapException extends RuntimeException {

    DateTimeOverlapException(String message) {
        super(message);
    }
}
