package ru.yandex.practicum.kanban.service.impl;

import ru.yandex.practicum.kanban.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String DEFAULT_DIRECTORY = "resources";
    private final Path filePath;

    public static final String DELIMITER = ",";
    protected static final int ID_INDEX = 0;
    public static final int TYPE_INDEX = 1;
    protected static final int NAME_INDEX = 2;
    protected static final int START_DATE_TIME_INDEX = 3;
    protected static final int END_DATE_TIME_INDEX = 4;
    protected static final int DURATION_INDEX = 5;
    protected static final int STATUS_INDEX = 6;
    protected static final int DESCRIPTION_INDEX = 7;
    private static final int EPIC_ID_INDEX = 8;
    private static final int NUMBER_OF_SUBTASKS_INDEX = 8;
    private static final int FILE_HEADER_INDEX = 1;
    private static final String FILE_HEADER =
            "ID,Type,Name,StartDateTime,EndDateTime,Duration,Status,Description,EpicID,NumberOfSubtasks,SubtaskIDs";

    public FileBackedTaskManager(String fileName) {
        filePath = Paths.get(DEFAULT_DIRECTORY, fileName);
    }

    public static FileBackedTaskManager loadFromFile(String fileName) throws ManagerLoadException {
        FileBackedTaskManager manager = new FileBackedTaskManager(fileName);

        try {
            List<String> fileLines = Files.readAllLines(manager.getFilePath());
            List<Task> tasksFromFile = fileLines.stream()
                    .skip(FILE_HEADER_INDEX)
                    .map(FileBackedTaskManager::fromString)
                    .toList();

            int maxID = 0;

            for (Task task : tasksFromFile) {
                int id = task.getId();

                switch (task.getType()) {
                    case TaskTypes.TASK -> manager.tasks.put(id, task);
                    case TaskTypes.EPIC -> manager.epics.put(id, (Epic) task);
                    case TaskTypes.SUBTASK -> manager.subtasks.put(id, (Subtask) task);
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
                writer.write(toString(task));
            }

            for (Epic epic : epics.values()) {
                writer.newLine();
                writer.write(toString(epic));
            }

            for (Subtask subtask : subtasks.values()) {
                writer.newLine();
                writer.write(toString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public String toString(Task task) {
        long duration = task.getDuration().toMinutes();
        return String.join(DELIMITER,
                task.getId().toString(),
                task.getType().toString(),
                task.getName(),
                task.getStartDateTime().toString(),
                task.getEndDateTime().toString(),
                Long.toString(duration),
                task.getStatus().toString(),
                task.getDescription());
    }

    public String toString(Subtask subtask) {
        return String.join(DELIMITER, toString((Task) subtask), subtask.getEpicID().toString());
    }

    public String toString(Epic epic) {
        StringBuilder builder = new StringBuilder(toString((Task) epic));
        builder.append(DELIMITER).append(epic.getAllSubtaskIDs().size());
        epic.getAllSubtaskIDs().forEach(id -> builder.append(DELIMITER).append(id.toString()));

        return builder.toString();
    }

    public static Task fromString(String taskAsString) {
        String[] fields = taskAsString.split(DELIMITER);

        Integer id = Integer.parseInt(fields[ID_INDEX]);
        TaskTypes type = TaskTypes.valueOf(fields[TYPE_INDEX]);
        String name = fields[NAME_INDEX];
        LocalDateTime startDateTime = LocalDateTime.parse(fields[START_DATE_TIME_INDEX]);
        Duration duration = Duration.ofMinutes(Long.parseLong(fields[DURATION_INDEX]));
        TaskStatus status = TaskStatus.valueOf(fields[STATUS_INDEX]);
        String description = fields[DESCRIPTION_INDEX];

        Task task = null;

        switch (type) {
            case TaskTypes.TASK -> task = new Task(name, description, startDateTime, duration);
            case TaskTypes.EPIC -> {
                Epic epic = new Epic(name, description);
                epic.setStartDateTime(startDateTime);
                epic.setDuration(duration);
                int numberOfSubtasks = Integer.parseInt(fields[NUMBER_OF_SUBTASKS_INDEX]);

                for (int subtaskIndex = 0; subtaskIndex < numberOfSubtasks; ++subtaskIndex) {
                    int parseIndex = subtaskIndex + NUMBER_OF_SUBTASKS_INDEX + 1;
                    Integer subtaskID = Integer.parseInt(fields[parseIndex]);
                    epic.addSubtaskID(subtaskID);
                }

                task = epic;
            }
            case TaskTypes.SUBTASK -> {
                Subtask subtask = new Subtask(name, description, startDateTime, duration);
                Integer epicID = Integer.parseInt(fields[EPIC_ID_INDEX]);
                subtask.setEpicID(epicID);
                task = subtask;
            }
        }

        task.setId(id);
        task.setStatus(status);
        task.setType(type);

        return task;
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