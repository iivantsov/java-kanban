package ru.yandex.practicum.kanban.service.impl;

public class ManagerLoadException extends RuntimeException {

    ManagerLoadException(String message) {
        super(message);
    }
}
