package ru.yandex.practicum.kanban.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.api.HistoryManager;

class InMemoryHistoryManagerTest {
    static private final String DEFAULT_TASK_DESCRIPTION = "Test Task";
    private HistoryManager historyManager;
    private Task task1;
    private final Duration defaultDuration = Duration.ofMinutes(30);

    @BeforeEach
    public void testInit() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Task1", DEFAULT_TASK_DESCRIPTION, LocalDateTime.now(), defaultDuration);
        task1.setId(1);
        historyManager.add(task1);
    }

    @Test
    public void testAddSuccessfullyAddsNotNullTaskToHistory() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "History not found!");
    }

    @Test
    public void testAddNotAddsNullTaskToHistory() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "History size is wrong!");
    }

    @Test
    public void testAddSavesTasksInOrder() {
        LocalDateTime task2StartDateTime = LocalDateTime.now().plus(task1.getDuration());
        Task task2 = new Task("Task2", DEFAULT_TASK_DESCRIPTION, task2StartDateTime, defaultDuration);
        task2.setId(2);

        LocalDateTime task3StartDateTime = LocalDateTime.now().plus(task2.getDuration());
        Task task3 = new Task("Task3", DEFAULT_TASK_DESCRIPTION, task3StartDateTime, defaultDuration);
        task3.setId(3);

        List<Task> tasks = new ArrayList<>(List.of(task1, task3, task2));

        historyManager.add(task3);
        historyManager.add(task2);

        List<Task> tasksInHistory = historyManager.getHistory();
        assertEquals(tasks, tasksInHistory, "Tasks in history have wrong order!");
    }

    @Test
    public void testAddDoesNotSaveDuplicates() {
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(history.size(),1);
    }

    @Test
    public void testRemoveSuccessfullyDeletesTaskFromHistory() {
        Integer task1Id = task1.getId();
        historyManager.remove(task1Id);

        LocalDateTime startDateTime = LocalDateTime.now().plus(task1.getDuration());
        Task task2ToReplaceTask1 = new Task("Task2", DEFAULT_TASK_DESCRIPTION, startDateTime, defaultDuration);
        task2ToReplaceTask1.setId(2);
        historyManager.add(task2ToReplaceTask1);

        for (Task task : historyManager.getHistory()) {
            assertNotEquals(task.getId(), task1Id);
        }
    }
}