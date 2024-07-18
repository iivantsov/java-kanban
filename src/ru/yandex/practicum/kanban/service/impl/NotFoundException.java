package ru.yandex.practicum.kanban.service.impl;

public class NotFoundException extends RuntimeException {

    NotFoundException(String message) {
        super(message);
    }
}