package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.service.impl.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    public InMemoryTaskManagerTest() {
        taskManager = new InMemoryTaskManager();
    }
}