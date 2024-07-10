package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.service.impl.FileBackedTaskManager;
import ru.yandex.practicum.kanban.service.impl.ManagerLoadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    public FileBackedTaskManagerTest() {
        taskManager = new FileBackedTaskManager("data.csv");
    }

    @Test
    public void testCreateEpicCreatesNotEmptyFile() throws IOException {
        Path filePath = taskManager.getFilePath();
        Assertions.assertTrue(Files.exists(filePath), "File does not exists!");
        Assertions.assertNotEquals(Files.size(filePath), 0, "File is empty!");
    }

    @Test
    public void testLoadFromFileProducesEmptyManagerIfFileIsEmpty() throws IOException {
        Path filePath = taskManager.getFilePath();
        Files.delete(filePath);
        Files.createFile(filePath);
        String fileName = filePath.getFileName().toString();
        FileBackedTaskManager managerFormFile = FileBackedTaskManager.loadFromFile(fileName);

        Assertions.assertTrue(managerFormFile.getAllTasks().isEmpty());
        Assertions.assertTrue(managerFormFile.getAllEpic().isEmpty());
        Assertions.assertTrue(managerFormFile.getAllEpic().isEmpty());
    }

    @Test
    public void testLoadFromNonExistentFileThrowsManagerLoadException() {
        String fileName = "non-existent-file.jpg";
        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(fileName));
    }

    @Test
    public void testLoadFromFileProducesEqualManager() {
        Path filePath = taskManager.getFilePath();
        String fileName = filePath.getFileName().toString();
        FileBackedTaskManager managerFormFile = FileBackedTaskManager.loadFromFile(fileName);

        Assertions.assertEquals(taskManager, managerFormFile, "Manager instances are not equal!");
    }

    @Test
    public void testFromStringProducesEqualTaskFromToStringOutput() {
        Task task = new Task("Easy", "Say Hello World!",
                LocalDateTime.now().plusMonths(1), Duration.ofMinutes(45));

        task.setId(123);
        task.setStatus(TaskStatus.DONE);

        String taskAsString = taskManager.toString(task);
        Task taskFromString = FileBackedTaskManager.fromString(taskAsString);

        Assertions.assertEquals(task.getId(), taskFromString.getId(),
                "IDs are not equal!");
        Assertions.assertEquals(task.getType(), taskFromString.getType(),
                "Types are not equal!");
        Assertions.assertEquals(task.getName(), taskFromString.getName(),
                "Names are not equal!");
        Assertions.assertEquals(task.getStatus(), taskFromString.getStatus(),
                "Statuses are not equal!");
        Assertions.assertEquals(task.getDescription(), taskFromString.getDescription(),
                "Descriptions are not equal!");
    }

    @Test
    public void testFromStringProducesEqualEpicFromToStringOutput() {
        String epicAsString = taskManager.toString(newEpic1);
        Epic epicFromString = (Epic) FileBackedTaskManager.fromString(epicAsString);

        Assertions.assertEquals(newEpic1.getId(), epicFromString.getId(),
                "IDs are not equal!");
        Assertions.assertEquals(newEpic1.getType(), epicFromString.getType(),
                "Types are not equal!");
        Assertions.assertEquals(newEpic1.getName(), epicFromString.getName(),
                "Names are not equal!");
        Assertions.assertEquals(newEpic1.getStatus(), epicFromString.getStatus(),
                "Statuses are not equal!");
        Assertions.assertEquals(newEpic1.getDescription(), epicFromString.getDescription(),
                "Descriptions are not equal!");
        Assertions.assertEquals(newEpic1.getAllSubtaskIDs(), epicFromString.getAllSubtaskIDs(),
                "Subtask IDs are not equal!");
    }

    @Test
    public void testFromStringProducesEqualSubtaskFromToStringOutput() {
        String subtaskAsString = taskManager.toString(newSubtask1);
        Subtask subtaskFromString = (Subtask) FileBackedTaskManager.fromString(subtaskAsString);

        Assertions.assertEquals(newSubtask1.getId(), subtaskFromString.getId(),
                "IDs are not equal!");
        Assertions.assertEquals(newSubtask1.getType(), subtaskFromString.getType(),
                "Types are not equal!");
        Assertions.assertEquals(newSubtask1.getName(), subtaskFromString.getName(),
                "Names are not equal!");
        Assertions.assertEquals(newSubtask1.getStatus(), subtaskFromString.getStatus(),
                "Statuses are not equal!");
        Assertions.assertEquals(newSubtask1.getDescription(), subtaskFromString.getDescription(),
                "Descriptions are not equal!");
        Assertions.assertEquals(newSubtask1.getEpicID(), subtaskFromString.getEpicID(),
                "Epic IDs are not equal!");
    }
}