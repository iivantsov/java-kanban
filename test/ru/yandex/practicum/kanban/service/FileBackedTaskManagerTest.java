package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.service.impl.FileBackedTaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private Epic epic;
    private Subtask subtask1;

    @BeforeEach
    public void testInit() throws IOException {
        manager = new FileBackedTaskManager("data.csv");
        Files.delete(manager.getFilePath());

        epic = new Epic("Kanban App", "Working on Kanban App for this week");
        Integer workEpicID = manager.createEpic(epic);

        Duration subtaskDuration = Duration.ofMinutes(30);

        subtask1 = new Subtask("Presentation", "Prepare a basic presentation layout",
                LocalDateTime.now(), subtaskDuration);

        subtask1.setEpicID(workEpicID);
        Integer workSubtask1ID = manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test report", "Prepare a backend test report",
                LocalDateTime.now().plus(subtaskDuration), subtaskDuration);

        subtask2.setEpicID(workEpicID);
        Integer workSubtask2ID = manager.createSubtask(subtask2);
    }

    @Test
    public void testCreateEpicCreatesNotEmptyFile() throws IOException {
        Path filePath = manager.getFilePath();
        Assertions.assertTrue(Files.exists(filePath), "File does not exists!");
        Assertions.assertNotEquals(Files.size(filePath), 0, "File is empty!");
    }

    @Test
    public void testLoadFromFileProducesEmptyManagerIfFileIsEmpty() throws IOException {
        Path filePath = manager.getFilePath();
        Files.delete(filePath);
        Files.createFile(filePath);
        String fileName = filePath.getFileName().toString();
        FileBackedTaskManager managerFormFile = FileBackedTaskManager.loadFromFile(fileName);

        Assertions.assertTrue(managerFormFile.getAllTasks().isEmpty());
        Assertions.assertTrue(managerFormFile.getAllEpic().isEmpty());
        Assertions.assertTrue(managerFormFile.getAllEpic().isEmpty());
    }

    @Test
    public void testLoadFromFileProducesEqualManager() {
        Path filePath = manager.getFilePath();
        String fileName = filePath.getFileName().toString();
        FileBackedTaskManager managerFormFile = FileBackedTaskManager.loadFromFile(fileName);

        Assertions.assertEquals(manager, managerFormFile, "Manager instances are not equal!");
    }

    @Test
    public void testFromStringProducesEqualTaskFromToStringOutput() {
        Task task = new Task("Easy", "Say Hello World!",
                LocalDateTime.now().plusMonths(1), Duration.ofMinutes(45));

        task.setId(123);
        task.setStatus(TaskStatus.DONE);

        String taskAsString = manager.toString(task);
        Task taskFromString = FileBackedTaskManager.fromString(taskAsString);

        Assertions.assertEquals(task.getId(), taskFromString.getId(), "IDs are not equal!");
        Assertions.assertEquals(task.getType(), taskFromString.getType(), "Types are not equal!");
        Assertions.assertEquals(task.getName(), taskFromString.getName(), "Names are not equal!");
        Assertions.assertEquals(task.getStatus(), taskFromString.getStatus(), "Statuses are not equal!");
        Assertions.assertEquals(task.getDescription(), taskFromString.getDescription(),
                "Descriptions are not equal!");
    }

    @Test
    public void testFromStringProducesEqualEpicFromToStringOutput() {
        String epicAsString = manager.toString(epic);
        Epic epicFromString = (Epic) FileBackedTaskManager.fromString(epicAsString);

        Assertions.assertEquals(epic.getId(), epicFromString.getId(), "IDs are not equal!");
        Assertions.assertEquals(epic.getType(), epicFromString.getType(), "Types are not equal!");
        Assertions.assertEquals(epic.getName(), epicFromString.getName(), "Names are not equal!");
        Assertions.assertEquals(epic.getStatus(), epicFromString.getStatus(), "Statuses are not equal!");
        Assertions.assertEquals(epic.getDescription(), epicFromString.getDescription(),
                "Descriptions are not equal!");
        Assertions.assertEquals(epic.getAllSubtaskIDs(), epicFromString.getAllSubtaskIDs(),
                "Subtask IDs are not equal!");
    }

    @Test
    public void testFromStringProducesEqualSubtaskFromToStringOutput() {
        String subtaskAsString = manager.toString(subtask1);
        Subtask subtaskFromString = (Subtask) FileBackedTaskManager.fromString(subtaskAsString);

        Assertions.assertEquals(subtask1.getId(), subtaskFromString.getId(), "IDs are not equal!");
        Assertions.assertEquals(subtask1.getType(), subtaskFromString.getType(), "Types are not equal!");
        Assertions.assertEquals(subtask1.getName(), subtaskFromString.getName(), "Names are not equal!");
        Assertions.assertEquals(subtask1.getStatus(), subtaskFromString.getStatus(), "Statuses are not equal!");
        Assertions.assertEquals(subtask1.getDescription(), subtaskFromString.getDescription(),
                "Descriptions are not equal!");
        Assertions.assertEquals(subtask1.getEpicID(), subtaskFromString.getEpicID(), "Epic IDs are not equal!");
    }
}