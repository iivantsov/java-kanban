package ru.yandex.practicum.kanban.service.api;

import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}