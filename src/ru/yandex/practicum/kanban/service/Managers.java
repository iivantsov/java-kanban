package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.service.api.*;
import ru.yandex.practicum.kanban.service.impl.*;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}