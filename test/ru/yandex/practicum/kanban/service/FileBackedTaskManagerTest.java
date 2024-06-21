package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.service.impl.FileBackedTaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;

    @BeforeEach
    public void testInit() throws IOException {
        manager = new FileBackedTaskManager("data.csv");
        Files.delete(manager.getFilePath());

        Epic workEpic = new Epic("Kanban App", "Working on Kanban App for this week");
        Integer workEpicID = manager.createEpic(workEpic);

        Subtask workSubtask1 = new Subtask("Presentation", "Prepare a basic presentation layout");
        workSubtask1.setEpicID(workEpicID);
        Integer workSubtask1ID = manager.createSubtask(workSubtask1);

        Subtask workSubtask2 = new Subtask("Test report", "Prepare a backend test report");
        workSubtask2.setEpicID(workEpicID);
        Integer workSubtask2ID = manager.createSubtask(workSubtask2);
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
}