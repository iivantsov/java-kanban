package ru.yandex.practicum.kanban.service.api;

import ru.yandex.practicum.kanban.model.*;

import java.util.List;
import java.util.ArrayList;

public interface TaskManager {

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    // Tasks methods
    ArrayList<Task> getAllTasks();

    Task getTaskByID(Integer id);

    Integer createTask(Task task);

    void updateTask(Task task);

    void removeTaskByID(Integer id);

    void removeAllTasks();

    // Subtasks methods
    ArrayList<Subtask> getAllSubtasks();

    Subtask getSubtaskByID(Integer id);

    Integer createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtaskByID(Integer subtaskID);

    void removeAllSubtasks();

    // Epics methods
    ArrayList<Epic> getAllEpic();

    Epic getEpicByID(Integer id);

    ArrayList<Subtask> getAllSubtasksByEpicID(Integer epicID);

    Integer createEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicByID(Integer epicID);

    void removeAllEpic();
}