package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.service.api.*;
import ru.yandex.practicum.kanban.service.impl.*;

public class Managers {
    private static final String FILE_NAME = "data.csv";

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(FILE_NAME);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}