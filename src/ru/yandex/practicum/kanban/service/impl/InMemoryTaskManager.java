package ru.yandex.practicum.kanban.service.impl;

import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.api.*;
import ru.yandex.practicum.kanban.service.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Integer nextID = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> tasksSortedByDateTime =
            new TreeSet<>(Comparator.comparing(Task::getStartDateTime));

    @Override
    public List<Task> getPrioritizedTasks() {
        return tasksSortedByDateTime.stream()
                .toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Tasks methods
    @Override
    public List<Task> getAllTasks() {
        return tasks.values().stream()
                .toList();
    }

    @Override
    public Task getTaskByID(Integer id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Integer createTask(Task task) {
        Integer id = nextID++;
        task.setId(id);
        tasks.put(id, task);

        if (validateByDateTime(task)) {
            tasksSortedByDateTime.add(task);
        }

        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task " + task.getName() + " was not created!");
        }
        tasks.put(task.getId(), task);

        if (validateByDateTime(task)) {
            tasksSortedByDateTime.add(task);
        }
    }

    @Override
    public void removeTaskByID(Integer id) {
        tasksSortedByDateTime.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeAllTasks() {
        for (Integer taskID : tasks.keySet()) {
            tasksSortedByDateTime.remove(tasks.get(taskID));
            historyManager.remove(taskID);
        }
        tasks.clear();
    }

    // Subtasks methods
    @Override
    public List<Subtask> getAllSubtasks() {
        return subtasks.values().stream()
                .toList();
    }

    @Override
    public Subtask getSubtaskByID(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        Integer epicID = subtask.getEpicID();
        if (!epics.containsKey(epicID)) {
            throw new IllegalArgumentException("Epic with ID=" + epicID + " was not created!");
        }
        Integer subtaskID = nextID++;
        subtask.setId(subtaskID);
        subtasks.put(subtaskID, subtask);

        if (validateByDateTime(subtask)) {
            tasksSortedByDateTime.add(subtask);
        }

        Epic epic = epics.get(epicID);
        epic.addSubtaskID(subtaskID);

        syncEpicStatus(epicID);
        computeEpicTiming(epicID);

        return subtaskID;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Subtask " + subtask.getName() + " was not created!");
        }
        subtasks.put(subtask.getId(), subtask);

        if (validateByDateTime(subtask)) {
            tasksSortedByDateTime.add(subtask);
        }

        syncEpicStatus(subtask.getEpicID());
        computeEpicTiming(subtask.getEpicID());
    }

    @Override
    public void removeSubtaskByID(Integer subtaskID) {
        if (!subtasks.containsKey(subtaskID)) {
            return;
        }
        Subtask subtask = subtasks.get(subtaskID);
        tasksSortedByDateTime.remove(subtask);
        Integer epicID = subtask.getEpicID();
        Epic epic = epics.get(epicID);
        epic.removeSubtaskByID(subtaskID);
        subtasks.remove(subtaskID);
        historyManager.remove(subtaskID);

        syncEpicStatus(epicID);
        computeEpicTiming(epicID);
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer subtaskID : subtasks.keySet()) {
            tasksSortedByDateTime.remove(subtasks.get(subtaskID));
            historyManager.remove(subtaskID);
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeAllSubtaskIDs();
            epic.setStatus(TaskStatus.NEW);
            epic.setStartDateTime(LocalDateTime.now());
            epic.setDuration(Duration.ZERO);
        }
    }

    // Epics methods
    @Override
    public List<Epic> getAllEpic() {
        return epics.values().stream()
                .toList();
    }

    @Override
    public Epic getEpicByID(Integer id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicID(Integer epicID) {
        if (!epics.containsKey(epicID)) {
            throw new IllegalArgumentException("Epic ID=" + epicID + " was not created!");
        }
        Epic epic = epics.get(epicID);

        return epic.getAllSubtaskIDs().stream()
                .map(subtasks::get)
                .toList();
    }

    @Override
    public Integer createEpic(Epic epic) {
        Integer id = nextID++;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        Integer id = epic.getId();
        if (!epics.containsKey(id)) {
            throw new IllegalArgumentException("Epic " + epic.getName() + " was not created!");
        }

        epics.put(id, epic);
        syncEpicStatus(id);
        computeEpicTiming(id);
    }

    @Override
    public void removeEpicByID(Integer epicID) {
        if (!epics.containsKey(epicID)) {
            return;
        }
        Epic epic = epics.get(epicID);

        for (Integer subtaskID : epic.getAllSubtaskIDs()) {
            tasksSortedByDateTime.remove(subtasks.get(subtaskID));
            subtasks.remove(subtaskID);
            historyManager.remove(subtaskID);
        }

        epics.remove(epicID);
        historyManager.remove(epicID);
    }

    @Override
    public void removeAllEpic() {
        for (Integer subtaskID : subtasks.keySet()) {
            tasksSortedByDateTime.remove(subtasks.get(subtaskID));
            historyManager.remove(subtaskID);
        }
        subtasks.clear();

        epics.keySet().forEach(historyManager::remove);
        epics.clear();
    }

    private void syncEpicStatus(Integer epicID) {
        List<Subtask> subtasksInEpic = getAllSubtasksByEpicID(epicID); // Also checks that epics contains epicID
        Epic epic = epics.get(epicID);
        if (subtasksInEpic.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int newSubtasksCnt = 0;
        int doneSubtasksCnt = 0;
        for (Subtask currSubtask : subtasksInEpic) {
            TaskStatus subtaskStatus = currSubtask.getStatus();
            if (subtaskStatus == TaskStatus.IN_PROGRESS) {
                break;
            } else if (subtaskStatus == TaskStatus.NEW) {
                ++newSubtasksCnt;
            } else if (subtaskStatus == TaskStatus.DONE) {
                ++doneSubtasksCnt;
            }
        }

        TaskStatus epicStatus;
        if (newSubtasksCnt == subtasksInEpic.size()) {
            epicStatus = TaskStatus.NEW;
        } else if (doneSubtasksCnt == subtasksInEpic.size()) {
            epicStatus = TaskStatus.DONE;
        } else {
            epicStatus = TaskStatus.IN_PROGRESS;
        }
        epic.setStatus(epicStatus);
    }

    private void computeEpicTiming(Integer epicID) {
        Epic epic = epics.get(epicID);

        List<Subtask> subtasksInEpic = getPrioritizedTasks().stream()
                .filter(task -> task.getType() == TaskTypes.SUBTASK)
                .map(task -> (Subtask) task)
                .filter(subtask -> subtask.getEpicID().equals(epicID))
                .toList();

        if (subtasksInEpic.isEmpty()) {
            return;
        }

        Duration epicDuration = subtasksInEpic.stream()
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setDuration(epicDuration);
        epic.setStartDateTime(subtasksInEpic.getFirst().getStartDateTime());
        epic.setEndDateTime(subtasksInEpic.getLast().getEndDateTime());
    }

    public static boolean checkDateTimeOverlap(Task task1, Task task2) {
        return task1.getStartDateTime().isBefore(task2.getEndDateTime()) &&
                task1.getEndDateTime().isAfter(task2.getStartDateTime());
    }

    private boolean validateByDateTime(Task task) throws DateTimeOverlapException {
        if (task.getStartDateTime() == null || task.getDuration() == null) {
            return false;
        }
        getPrioritizedTasks().stream()
                .filter(t -> !t.equals(task))
                .filter(t -> checkDateTimeOverlap(task, t))
                .findFirst()
                .ifPresent(t -> {
                    throw new DateTimeOverlapException("\n" + task + "\n" + t);
                });

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InMemoryTaskManager that)) {
            return false;
        }

        return Objects.equals(nextID, that.nextID)
                && Objects.equals(tasks, that.tasks)
                && Objects.equals(epics, that.epics)
                && Objects.equals(subtasks, that.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nextID, tasks, epics, subtasks);
    }
}