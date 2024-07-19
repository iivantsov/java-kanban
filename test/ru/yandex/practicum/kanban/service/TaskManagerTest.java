package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.service.api.TaskManager;
import ru.yandex.practicum.kanban.service.impl.DateTimeOverlapException;
import ru.yandex.practicum.kanban.service.impl.InMemoryTaskManager;
import ru.yandex.practicum.kanban.service.impl.NotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected Task task1;
    protected Integer task1ID;
    protected Task task2;

    protected Epic newEpic1;
    protected Integer newEpic1ID;
    static protected final String DEFAULT_EPIC_DESCRIPTION = "Test Epic";
    protected Subtask newSubtask1;
    protected Integer newSubtask1ID;
    protected Subtask newSubtask2;
    protected Integer newSubtask2ID;

    protected LocalDateTime startDateTime;
    protected final Duration duration = Duration.ofMinutes(30);

    @BeforeEach
    public void testInit() {
        startDateTime = LocalDateTime.now();

        newEpic1 = new Epic("Epic#1", DEFAULT_EPIC_DESCRIPTION);
        newEpic1ID = taskManager.createEpic(newEpic1);

        newSubtask1 = new Subtask("Subtask#1", "Test Subtask", startDateTime, duration);
        startDateTime = startDateTime.plus(duration);
        newSubtask1.setEpicID(newEpic1ID);
        newSubtask1ID = taskManager.createSubtask(newSubtask1);

        newSubtask2 = new Subtask("Subtask#2", "Test Subtask", startDateTime, duration);
        startDateTime = startDateTime.plus(duration);
        newSubtask2.setEpicID(newEpic1ID);
        newSubtask2ID = taskManager.createSubtask(newSubtask2);

        LocalDateTime task2Start = LocalDateTime.of(2024, Month.JUNE, 21, 17,35);
        task2 = new Task("Violin", "Try to play Vivaldi \"The Four Seasons\"", task2Start, duration);
        taskManager.createTask(task2);

        LocalDateTime task1Start = LocalDateTime.of(2024, Month.JUNE, 9, 10,0);
        task1 = new Task("Cleaning", "Vacuum and wash the floors at home", task1Start, duration);
        task1ID = taskManager.createTask(task1);
    }

    // History
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
        taskManager.getEpicByID(newEpic1ID);

        taskManager.getHistory().forEach(task -> assertNotEquals(task.getDescription(), DEFAULT_EPIC_DESCRIPTION,
                "Previous Epic view stores in history!"));
    }

    @Test
    public void testRemoveSubtaskByIdDeletesViewedSubtaskFromHistory() {
        // View all created Subtasks
        Subtask registeredSubtask1 = taskManager.getSubtaskByID(newSubtask1ID);
        taskManager.getSubtaskByID(newSubtask2ID);
        // Remove Subtask1 from Epic1
        taskManager.removeSubtaskByID(newSubtask1ID);

        taskManager.getHistory().forEach(task -> assertNotEquals(task, registeredSubtask1,
                "History contains removed Subtask1!"));
    }

    @Test
    public void testRemoveAllEpicsAndTaskProvideEmptyHistory() {
        taskManager.removeAllTasks();
        taskManager.removeAllEpic();

        assertNull(taskManager.getHistory(), "History is not empty!");
    }

    // Prioritized tasks
    @Test
    public void testGetPrioritizedTasksProvidesListOfTasksSortedByDateTime() {
        List<Task> expectedTaskList = List.of(task1, task2, newSubtask1, newSubtask2);
        assertEquals(expectedTaskList, taskManager.getPrioritizedTasks(), "Wrong task priority!");
    }

    // Date&Time overlap
    @Test
    public void testCreateTaskWithDateTimeOverlapThrowsDateTimeOverlapException() {
        Task task3 = new Task("Task3", "Overlap test", task1.getStartDateTime(),  task1.getDuration());
        assertThrows(DateTimeOverlapException.class, () -> taskManager.createTask(task3));
    }

    @Test
    public void testCheckDateTimeOverlapReturnsTrueInCaseOfOverlap() {
        LocalDateTime task1DateTime = LocalDateTime.of(2024,Month.JULY,11,19,0);
        Task task1 = new Task("Task1", "checkDateTimeOverlap test", task1DateTime, duration);
        LocalDateTime task2DateTime = LocalDateTime.of(2024,Month.JULY,11,18,30);
        Task task2 = new Task("Task2", "checkDateTimeOverlap test", task2DateTime, duration);
        // task1 @            19:00 ---- 19:30
        // task2 @ 18:30 ---- 19:00
        assertFalse(InMemoryTaskManager.checkDateTimeOverlap(task1, task2),
                "Task1 overlaps with Task2!");
        assertFalse(InMemoryTaskManager.checkDateTimeOverlap(task2, task1),
                "Task2 overlaps with Task1!");
    }

    @Test
    public void testCheckDateTimeOverlapReturnsFalseInCaseOfNoOverlap() {
        LocalDateTime task1DateTime = LocalDateTime.of(2024,Month.JULY,11,19,0);
        Task task1 = new Task("Task1", "checkDateTimeOverlap test", task1DateTime, duration);
        LocalDateTime task2DateTime = LocalDateTime.of(2024,Month.JULY,11,19,15);
        Task task2 = new Task("Task2", "checkDateTimeOverlap test", task2DateTime, duration);
        // task1 @ 19:00 ---- 19:30
        // task2 @      19:15 ----- 19:45
        assertTrue(InMemoryTaskManager.checkDateTimeOverlap(task1, task2),
                "Task1 does not overlap with Task2");
        assertTrue(InMemoryTaskManager.checkDateTimeOverlap(task2, task1),
                "Task2 does not overlap with Task1");
    }

    // Tasks
    @Test
    void testGetAllTasksProvidesListOfRegisteredTasks() {
        List<Task> expectedTaskList = List.of(task2, task1);
        assertEquals(expectedTaskList, taskManager.getAllTasks(), "Wrong!");
    }

    @Test
    public void testCreateOneTaskAndOneEpicWithTwoSubtasksSuccessfullyCreatesAllItemsThatCanBeGetById() {
        // Task
        Task newTask = new Task("Task", "Test Task", startDateTime, duration);
        startDateTime = startDateTime.plus(duration);
        Integer newTaskID = taskManager.createTask(newTask);
        Task registeredTask = taskManager.getTaskByID(newTaskID);

        assertNotNull(registeredTask, "Task not found!");
        assertEquals(newTask, registeredTask, "Added and stored Tasks are not equals!");

        // Epic & Subtasks
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
    public void testUpdateTaskStatusAndDurationChangesTheseDataInTaskManager() {
        task1.setStatus(TaskStatus.DONE);
        task1.setDuration(Duration.ofMinutes(15));
        taskManager.updateTask(task1);
        Task registeredTask1 = taskManager.getTaskByID(task1ID);

        assertEquals(task1.getStatus(), registeredTask1.getStatus(), "Status was not updated!");
        assertEquals(task1.getDuration(), registeredTask1.getDuration(), "Duration was not updated!");
    }

    @Test
    public void testGetRemovedTaskByIdThrowsNotFoundException() {
        taskManager.removeTaskByID(task1ID);

        assertThrows(NotFoundException.class, () -> taskManager.getTaskByID(task1ID),
                "Task was not deleted from TaskManager!");
    }

    @Test
    public void testRemoveAllTaskDeletesAllTasksFromTaskManager() {
        taskManager.removeAllTasks();
        List<Task> registeredTasks = taskManager.getAllTasks();

        assertTrue(registeredTasks.isEmpty(), "TaskManager have non-empty Task list!");
    }

    // Subtasks and Epics
    @Test
    public void testGetAllSubtasksProvidesListOfRegisteredSubtasks() {
        List<Subtask> expectedSubtasks = List.of(newSubtask1, newSubtask2);
        List<Subtask> registeredSubtasks = taskManager.getAllSubtasks();

        assertEquals(expectedSubtasks, registeredSubtasks, "Expected Subtasks list not equals to actual!");
    }

    @Test
    public void testGetAllEpicProvidesListOfRegisteredEpics() {
        Epic newEpic2 = new Epic("Epic#1", DEFAULT_EPIC_DESCRIPTION);
        taskManager.createEpic(newEpic2);
        List<Epic> expectedEpics = List.of(newEpic1, newEpic2);
        List<Epic> registeredEpics = taskManager.getAllEpic();

        assertEquals(expectedEpics, registeredEpics, "Expected Epics list not equals to actual!");
    }

    @Test
    public void testUpdateSubtaskStatusAndDurationChangesTheseDataInTaskManager() {
        newSubtask1.setStatus(TaskStatus.DONE);
        newSubtask1.setDuration(Duration.ofMinutes(15));
        taskManager.updateSubtask(newSubtask1);
        Subtask registeredSubtask1 = taskManager.getSubtaskByID(newSubtask1ID);

        assertEquals(newSubtask1.getStatus(), registeredSubtask1.getStatus(), "Status was not updated!");
        assertEquals(newSubtask1.getDuration(), registeredSubtask1.getDuration(), "Duration was not updated!");
    }

    @Test
    public void testUpdateEpicStartTimeHaveNoEffect() { // Epic StartTime is calculated based on it's Subtasks
        LocalDateTime epicStartDateTime = newEpic1.getStartDateTime();
        newEpic1.setStartDateTime(LocalDateTime.now());
        taskManager.updateEpic(newEpic1);

        assertEquals(newEpic1.getStartDateTime(),epicStartDateTime);
    }

    @Test
    public void testRemoveSubtaskByIdDeletesItFromTaskManager() {
        taskManager.removeSubtaskByID(newSubtask1ID);
         List<Subtask> registeredSubtasks = taskManager.getAllSubtasks();

         assertFalse(registeredSubtasks.contains(newSubtask1), "Subtask1 was not deleted from TaskManager!");
    }

    @Test
    public void testRemoveEpicByIdDeletesItFromTaskManager() {
        taskManager.removeEpicByID(newEpic1ID);
        List<Epic> registeredEpics = taskManager.getAllEpic();

        assertFalse(registeredEpics.contains(newEpic1), "Epic was not deleted from TaskManager!");
    }

    @Test
    public void testRemoveAllSubtaskDeletesAllSubtaskFromTaskManager() {
        taskManager.removeAllSubtasks();
        List<Subtask> registeredSubtasks = taskManager.getAllSubtasks();

        assertTrue(registeredSubtasks.isEmpty(), "TaskManager have non-empty Subtask list!");
    }

    @Test
    public void testRemoveAllEpicDeletesAllEpicFromTaskManager() {
        taskManager.removeAllEpic();
        List<Epic> registeredEpic = taskManager.getAllEpic();

        assertTrue(registeredEpic.isEmpty(), "TaskManager have non-empty Epic list!");
    }

    @Test
    public void testRemoveAllSubtasksClearsEpicSubtaskIDsList() {
        taskManager.removeAllSubtasks();
        Epic registeredEpic = taskManager.getEpicByID(newEpic1ID);
        List<Integer> subtaskIDs = registeredEpic.getAllSubtaskIDs();

        assertTrue(subtaskIDs.isEmpty(), "Subtask IDs list for Epic1 is not empty!");
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
    public void testEpicHaveStatusNewIfAllSubtasksHaveStatusNew() {
        assertEquals(newEpic1.getStatus(),TaskStatus.NEW,
                "Epic1 status=" + newEpic1.getStatus() + "(NEW expected)");
    }

    @Test
    public void testEpicHaveStatusDoneIfAllSubtasksHaveStatusDone() {
        newSubtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(newSubtask1);
        newSubtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(newSubtask2);

        assertEquals(newEpic1.getStatus(),TaskStatus.DONE,
                "Epic1 status=" + newEpic1.getStatus() + "(DONE expected)");
    }

    @Test
    public void testCreateNewSubtaskAndUpdateExistedSubtaskWithDoneStatusChangesEpicStatusToInProgress() {
        newSubtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(newSubtask1);

        assertEquals(newEpic1.getStatus(),TaskStatus.IN_PROGRESS,
                "Epic1 status=" + newEpic1.getStatus() + "(IN_PROGRESS expected)");
    }

    @Test
    public void testCreateNewSubtaskAndUpdateExistedSubtaskWithInProgressStatusChangesEpicStatusToInProgress() {
        newSubtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(newSubtask1);

        assertEquals(newEpic1.getStatus(),TaskStatus.IN_PROGRESS,
                "Epic1 status=" + newEpic1.getStatus() + "(IN_PROGRESS expected)");
    }

    @Test
    public void testRemoveDoneSubtaskFromEpicWithNewAndDoneSubtasksChangesEpicStatusToNew() {
        newSubtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(newSubtask1);
        taskManager.removeSubtaskByID(newSubtask1ID);

        assertEquals(newEpic1.getStatus(),TaskStatus.NEW,
                "Epic1 status=" + newEpic1.getStatus() + "(NEW expected)");
    }

    @Test
    public void testEpicEndTimeEqualsLastSubtaskEndTime() {
        Subtask lastSubtaskInEpic1 = newEpic1.getAllSubtaskIDs().stream()
                .map(id -> taskManager.getSubtaskByID(id))
                .toList()
                .getLast();

        assertEquals(newEpic1.getEndDateTime(), lastSubtaskInEpic1.getEndDateTime(), "Epic end time is wrong!");
    }
}
