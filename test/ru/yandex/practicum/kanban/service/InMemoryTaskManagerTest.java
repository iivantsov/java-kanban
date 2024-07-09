package ru.yandex.practicum.kanban.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.api.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Epic newEpic1;
    private Integer newEpic1ID;
    static private final String NEW_EPIC1_DESCRIPTION = "Test Epic";
    private Subtask newSubtask1;
    private Integer newSubtask1ID;
    private LocalDateTime startDateTime;
    private final Duration defaultDuration = Duration.ofMinutes(30);

    @BeforeEach
    public void testInit() {
        taskManager = Managers.getDefault();
        startDateTime = LocalDateTime.now();

        newEpic1 = new Epic("Epic#1", NEW_EPIC1_DESCRIPTION);
        newEpic1ID = taskManager.createEpic(newEpic1);

        newSubtask1 = new Subtask("Subtask#1", "Test Subtask", startDateTime, defaultDuration);
        startDateTime = startDateTime.plus(defaultDuration);
        newSubtask1.setEpicID(newEpic1ID);
        newSubtask1ID = taskManager.createSubtask(newSubtask1);
    }

    @Test
    public void testCreateOneTaskAndOneEpicWithTwoSubtasksSuccessfullyCreatesAllItemsThatCanBeGetById() {
        // Task
        Task newTask = new Task("Task", "Test Task", startDateTime, defaultDuration);
        startDateTime = startDateTime.plus(defaultDuration);
        Integer newTaskID = taskManager.createTask(newTask);
        Task registeredTask = taskManager.getTaskByID(newTaskID);

        assertNotNull(registeredTask, "Task not found!");
        assertEquals(newTask, registeredTask, "Added and stored Tasks are not equals!");

        // Epic & Subtasks
        Subtask newSubtask2 = new Subtask("Subtask#2", "Test Subtask", startDateTime, defaultDuration);
        startDateTime = startDateTime.plus(defaultDuration);
        newSubtask2.setEpicID(newEpic1ID);
        Integer newSubtask2ID = taskManager.createSubtask(newSubtask2);

        Epic registeredEpic = taskManager.getEpicByID(newEpic1ID);
        List<Integer> subtaskIDsInRegisteredEpic = registeredEpic.getAllSubtaskIDs();
        Subtask registeredSubtask1 = taskManager.getSubtaskByID(newSubtask1ID);
        Subtask registeredSubtask2 = taskManager.getSubtaskByID(newSubtask2ID);

        assertNotNull(registeredEpic, "Epic not found!");
        assertEquals(newEpic1, registeredEpic, "Added and stored Epics are not equals!");
        assertNotNull(registeredSubtask1, "Subtask#1 not found!");
        assertEquals(newSubtask1, registeredSubtask1, "Added and stored Subtask#1 are not equals!");
        assertEquals(2, subtaskIDsInRegisteredEpic.size(), "Wrong Subtasks amount in Epic!");
        assertTrue(subtaskIDsInRegisteredEpic.contains(registeredSubtask1.getId()),
                "Subtask id=" + registeredSubtask1 + " is not contained in Epic");
        assertTrue(subtaskIDsInRegisteredEpic.contains(registeredSubtask2.getId()),
                "Subtask id=" + registeredSubtask2 + " is not contained in Epic");
    }

    @Test
    public void testCreateEpicWithOneSubtaskDoesNotChangeIdNameDescriptionStatus() {
        Epic registeredEpic = taskManager.getEpicByID(newEpic1ID);
        List<Integer> subtasksInRegisteredEpic = registeredEpic.getAllSubtaskIDs();

        assertEquals(newEpic1.getName(), registeredEpic.getName(),
                "Added and stored Epics names are different!");
        assertEquals(newEpic1.getDescription(), registeredEpic.getDescription(),
                "Added and stored Epics descriptions are different!");
        assertEquals(newEpic1.getStatus(), registeredEpic.getStatus(),
                "Added and stored Epics descriptions are different!");

        Integer registeredSubtaskID = subtasksInRegisteredEpic.getFirst();
        Subtask registeredSubtask = taskManager.getSubtaskByID(registeredSubtaskID);

        assertEquals(newSubtask1.getName(), registeredSubtask.getName(),
                "Added and stored Subtask names are different!");
        assertEquals(newSubtask1.getDescription(), registeredSubtask.getDescription(),
                "Added and stored Subtask descriptions are different!");
        assertEquals(newSubtask1.getStatus(), registeredSubtask.getStatus(),
                "Added and stored Subtask descriptions are different!");
        assertEquals(newSubtask1.getId(), registeredSubtask.getId(),
                "Added and stored Subtask IDs are different!");
    }

    @Test
    public void testCreateNewSubtaskAndUpdateExistedSubtaskWithDoneStatusChangesEpicStatusToInProgress() {
        Subtask newSubtask2 = new Subtask("Subtask#2", "Test Subtask", startDateTime, defaultDuration);
        startDateTime = startDateTime.plus(defaultDuration);
        newSubtask2.setEpicID(newEpic1ID);
        Integer newSubtask2ID = taskManager.createSubtask(newSubtask2);
        newSubtask1.setStatus(TaskStatus.DONE);

        taskManager.updateSubtask(newSubtask1);

        assertEquals(newEpic1.getStatus(),TaskStatus.IN_PROGRESS,
                "Epic1 status=" + newEpic1.getStatus() + "(IN_PROGRESS expected)");
    }

    @Test
    public void testRemoveDoneSubtaskFromEpicWithNewAndDoneSubtasksChangesEpicStatusToNew() {
        Subtask newSubtask2 = new Subtask("Subtask#2", "Test Subtask", startDateTime, defaultDuration);
        startDateTime = startDateTime.plus(defaultDuration);
        newSubtask2.setEpicID(newEpic1ID);
        Integer newSubtask2ID = taskManager.createSubtask(newSubtask2);
        newSubtask1.setStatus(TaskStatus.DONE);

        taskManager.updateSubtask(newSubtask1);
        taskManager.removeSubtaskByID(newSubtask1ID);

        assertEquals(newEpic1.getStatus(),TaskStatus.NEW,
                "Epic1 status=" + newEpic1.getStatus() + "(NEW expected)");
    }

    @Test
    public void testRemoveAllSubtasksClearsEpicSubtaskIDsList() {
        taskManager.removeAllSubtasks();
        Epic registeredEpic = taskManager.getEpicByID(newEpic1ID);
        List<Integer> subtaskIDs = registeredEpic.getAllSubtaskIDs();

        assertTrue(subtaskIDs.isEmpty(), "Subtask IDs list for Epic1 is not empty!");
    }

    @Test
    public void testGetHistoryReturnsUnchangedEpicAndSubtaskAfterTheyWereUpdatedWithoutGetById() {
        Epic registeredEpic = taskManager.getEpicByID(newEpic1ID);
        List<Integer> subtasksInRegisteredEpic = registeredEpic.getAllSubtaskIDs();
        Integer registeredSubtaskID = subtasksInRegisteredEpic.getFirst();
        Subtask registeredSubtask = taskManager.getSubtaskByID(registeredSubtaskID);
        List<Task> history = taskManager.getHistory();

        String prevEpicDescription = registeredEpic.getDescription();
        registeredEpic.setDescription("Subtask.ru.yandex.practicum.kanban.Main Test Epic");
        taskManager.updateEpic(registeredEpic);

        TaskStatus prevSubtaskStatus = registeredSubtask.getStatus();
        registeredSubtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(registeredSubtask);

        for (Task task : history) {
            if (task instanceof Epic) {
                assertNotEquals(task.getDescription(), prevEpicDescription,
                        "Epic description changed in History after update without get by ID!");
            } else if (task instanceof Subtask) {
                assertNotEquals(task.getStatus(), prevSubtaskStatus,
                        "Subtask status changed in History after update without get by ID!");
            }
        }
    }

    @Test
    public void testGetHistoryReturnsOnlyLastEpicView() {
        // 1st view
        Epic registeredEpic = taskManager.getEpicByID(newEpic1ID);
        // Update
        registeredEpic.setDescription("Test Epic for getHistory");
        taskManager.updateEpic(registeredEpic);
        // 2nd view
        Epic registeredEpicUpdated = taskManager.getEpicByID(newEpic1ID);

        for (Task task : taskManager.getHistory()) {
            String description = task.getDescription();
            assertNotEquals(description, NEW_EPIC1_DESCRIPTION, "Previous Epic view stores in history!");
        }
    }

    @Test
    public void testRemoveSubtaskByIdDeletesViewedSubtaskFromHistory() {
        // Add new Subtask2 to Epic1
        Subtask newSubtask2 = new Subtask("Subtask#2","Test Subtask", startDateTime, defaultDuration);
        startDateTime = startDateTime.plus(defaultDuration);
        newSubtask2.setEpicID(newEpic1ID);
        Integer newSubtask2ID = taskManager.createSubtask(newSubtask2);
        // View all created Subtasks
        Subtask registeredSubtask1 = taskManager.getSubtaskByID(newSubtask1ID);
        Subtask registeredSubtask2 = taskManager.getSubtaskByID(newSubtask2ID);
        // Remove Subtask1 from Epic1
        taskManager.removeSubtaskByID(newSubtask1ID);

        for (Task task : taskManager.getHistory()) {
            assertNotEquals(task, registeredSubtask1,"History contains removed Subtask1!");
        }
    }
}