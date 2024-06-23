package ru.yandex.practicum.kanban.service.impl;

import ru.yandex.practicum.kanban.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String FILE_HEADER = "ID,Type,Name,Status,Description,EpicID,NumberOfSubtasks,SubtaskIDs";
    private static final String DEFAULT_DIRECTORY = "resources";
    private final Path filePath;

    public FileBackedTaskManager(String fileName) {
        filePath = Paths.get(DEFAULT_DIRECTORY, fileName);
    }

    public static FileBackedTaskManager loadFromFile(String fileName) throws ManagerLoadException {
        FileBackedTaskManager manager = new FileBackedTaskManager(fileName);

        try {
            List<String> fileLines = Files.readAllLines(manager.getFilePath());
            fileLines.remove(FILE_HEADER); // Remove Header
            int maxID = 0;
            int id = 0;

            for (String line : fileLines) {
                String[] fields = line.split(Task.DELIMITER);
                TaskTypes type = TaskTypes.valueOf(fields[Task.TYPE_INDEX]);

                switch (type) {
                    case TaskTypes.TASK -> {
                        Task task = Task.fromString(line);
                        id = task.getId();
                        manager.tasks.put(id, task);
                    }
                    case TaskTypes.EPIC -> {
                        Epic epic = Epic.fromString(line);
                        id = epic.getId();
                        manager.epics.put(id, epic);
                    }
                    case TaskTypes.SUBTASK -> {
                        Subtask subtask = Subtask.fromString(line);
                        id = subtask.getId();
                        manager.subtasks.put(id, subtask);
                    }
                }

                if (id > maxID) {
                    maxID = id;
                }
            }

            manager.nextID = maxID + 1;
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }

        return manager;
    }

    public Path getFilePath() {
        return filePath;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(FILE_HEADER);

            for (Task task : tasks.values()) {
                writer.newLine();
                writer.write(task.toString());
            }

            for (Epic epic : epics.values()) {
                writer.newLine();
                writer.write(epic.toString());
            }

            for (Subtask subtask : subtasks.values()) {
                writer.newLine();
                writer.write(subtask.toString());
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    // Tasks methods
    @Override
    public Integer createTask(Task task) {
        Integer id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskByID(Integer id) {
        super.removeTaskByID(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    // Subtasks methods
    @Override
    public Integer createSubtask(Subtask subtask) {
        Integer id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskByID(Integer subtaskID) {
        super.removeSubtaskByID(subtaskID);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    // Epics methods
    @Override
    public Integer createEpic(Epic epic) {
        Integer id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicByID(Integer epicID) {
        super.removeEpicByID(epicID);
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }
}