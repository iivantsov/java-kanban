package ru.yandex.practicum.kanban.service.api;

import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.impl.NotFoundException;

import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    // Tasks methods
    List<Task> getAllTasks();

    Task getTaskByID(Integer id) throws NotFoundException;

    Integer createTask(Task task);

    void updateTask(Task task);

    void removeTaskByID(Integer id);

    void removeAllTasks();

    // Subtasks methods
    List<Subtask> getAllSubtasks();

    Subtask getSubtaskByID(Integer id) throws NotFoundException;

    Integer createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtaskByID(Integer subtaskID);

    void removeAllSubtasks();

    // Epics methods
    List<Epic> getAllEpic();

    Epic getEpicByID(Integer id) throws NotFoundException;

    List<Subtask> getAllSubtasksByEpicID(Integer epicID);

    Integer createEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicByID(Integer epicID);

    void removeAllEpic();
}