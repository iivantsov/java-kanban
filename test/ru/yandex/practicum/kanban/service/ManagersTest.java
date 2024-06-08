package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ru.yandex.practicum.kanban.service.api.*;

class ManagersTest {

    @Test
    public void testGetDefaultReturnsValidTaskAndHistoryManagerObjects()
    {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager, "Invalid ru.yandex.practicum.kanban.service.api.TaskManager object!");
        assertNotNull(historyManager, "Invalid ru.yandex.practicum.kanban.service.api.HistoryManager object!");
    }
}