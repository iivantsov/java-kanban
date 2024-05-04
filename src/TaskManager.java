import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private Integer nextID = 1;

    private final HashMap<Integer, Task> idToTask = new HashMap<>();
    private final HashMap<Integer, Epic> idToEpic = new HashMap<>();
    private final HashMap<Integer, Subtask> idToSubtask = new HashMap<>();

    // Tasks methods
    Collection<Task> getAllTasks() {
        return idToTask.values();
    }

    Task getTask(Integer id) {
        return idToTask.get(id);
    }

    Integer create(@NotNull Task task) {
        Integer id = nextID;
        ++nextID;

        task.setId(id);
        idToTask.put(id, task);

        return id;
    }

    void update(@NotNull Task task) {
        if (Objects.equals(task.getId(), Task.INVALID_ID)) {
            throw new IllegalArgumentException("Task " + task.getName() + " was not created!");
        }

        idToTask.put(task.getId(), task);
    }

    void removeTask(Integer id) {
        idToTask.remove(id);
    }

    void removeAllTasks() {
        idToTask.clear();
    }

    // Subtasks methods
    Collection<Subtask> getAllSubtasks() {
        return idToSubtask.values();
    }

    Subtask getSubtask(Integer id) {
        return idToSubtask.get(id);
    }

    Integer create(Subtask subtask, Integer epicID) {
        if (!idToEpic.containsKey(epicID)) {
            throw new IllegalArgumentException("Epic with ID=" + epicID + " was not created!");
        }

        Integer subtaskID = nextID;
        ++nextID;

        subtask.setId(subtaskID);
        subtask.setEpicID(epicID);
        idToSubtask.put(subtaskID, subtask);

        Epic epic = idToEpic.get(epicID);
        epic.addSubtaskID(subtaskID);

        return subtaskID;
    }

    void update(@NotNull Subtask subtask) {
        if (Objects.equals(subtask.getId(), Task.INVALID_ID)) {
            throw new IllegalArgumentException("Subtask " + subtask.getName() + " was not created!");
        }

        idToSubtask.put(subtask.getId(), subtask);
        syncEpicStatus(subtask.getEpicID());
    }

    void removeSubtask(Integer subtaskID) {
        if (!idToSubtask.containsKey(subtaskID)) {
            return;
        }

        Subtask subtask = idToSubtask.get(subtaskID);
        Integer epicID = subtask.getEpicID();
        Epic epic = idToEpic.get(epicID);
        epic.removeSubtaskID(subtaskID);

        idToSubtask.remove(subtaskID);

        syncEpicStatus(epicID);
    }

    void removeAllSubtasks() {
        idToSubtask.clear();

        for (Epic epic : idToEpic.values()) {
            epic.removeAllSubtaskIDs();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    // Epics methods
    Collection<Epic> getAllEpic() {
        return idToEpic.values();
    }

    Epic getEpic(Integer id) {
        return idToEpic.get(id);
    }

    ArrayList<Subtask> getAllSubtasksInEpic(Integer epicID) {
        ArrayList<Subtask> subtasksInEpic = new ArrayList<>();

        for (Subtask subtask : idToSubtask.values()) {
            if (subtask.getEpicID().equals(epicID)) {
                subtasksInEpic.add(subtask);
            }
        }

        return subtasksInEpic;
    }

    Integer create(@NotNull Epic epic) {
        Integer id = nextID;
        ++nextID;

        epic.setId(id);
        idToEpic.put(id, epic);

        return id;
    }

    void update(@NotNull Epic epic) {
        if (Objects.equals(epic.getId(), Task.INVALID_ID)) {
            throw new IllegalArgumentException("Epic " + epic.getName() + " was not created!");
        }

        idToEpic.put(epic.getId(), epic);
    }

    void removeEpic(Integer epicID) {
        if (!idToEpic.containsKey(epicID)) {
            return;
        }

        Epic epic = idToEpic.get(epicID);

        for (Integer subtaskID : epic.getAllSubtaskIDs()) {
            idToSubtask.remove(subtaskID);
        }

        idToEpic.remove(epicID);
    }

    void removeAllEpic() {
        idToSubtask.clear();
        idToEpic.clear();
    }

    private void syncEpicStatus(Integer epicID) {
        Epic epic = idToEpic.get(epicID);
        ArrayList<Subtask> subtasksInEpic = getAllSubtasksInEpic(epicID);

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
}