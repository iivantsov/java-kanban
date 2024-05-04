import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private Integer nextID = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // Tasks methods
    Collection<Task> getAllTasks() {
        return tasks.values();
    }

    Task getTaskByID(Integer id) {
        return tasks.get(id);
    }

    Integer create(Task task) {
        Integer id = nextID++;

        task.setId(id);
        tasks.put(id, task);

        return id;
    }

    void update(Task task) {
        if (Objects.equals(task.getId(), Task.INVALID_ID)) {
            throw new IllegalArgumentException("Task " + task.getName() + " was not created!");
        }

        tasks.put(task.getId(), task);
    }

    void removeTaskByID(Integer id) {
        tasks.remove(id);
    }

    void removeAllTasks() {
        tasks.clear();
    }

    // Subtasks methods
    Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    Subtask getSubtaskByID(Integer id) {
        return subtasks.get(id);
    }

    Integer create(Subtask subtask, Integer epicID) {
        if (!epics.containsKey(epicID)) {
            throw new IllegalArgumentException("Epic with ID=" + epicID + " was not created!");
        }

        Integer subtaskID = nextID++;

        subtask.setId(subtaskID);
        subtask.setEpicID(epicID);
        subtasks.put(subtaskID, subtask);

        Epic epic = epics.get(epicID);
        epic.addSubtaskID(subtaskID);

        return subtaskID;
    }

    void update(Subtask subtask) {
        if (Objects.equals(subtask.getId(), Task.INVALID_ID)) {
            throw new IllegalArgumentException("Subtask " + subtask.getName() + " was not created!");
        }

        subtasks.put(subtask.getId(), subtask);
        syncEpicStatus(subtask.getEpicID());
    }

    void removeSubtaskByID(Integer subtaskID) {
        if (!subtasks.containsKey(subtaskID)) {
            return;
        }

        Subtask subtask = subtasks.get(subtaskID);
        Integer epicID = subtask.getEpicID();
        Epic epic = epics.get(epicID);
        epic.removeSubtaskByID(subtaskID);

        subtasks.remove(subtaskID);

        syncEpicStatus(epicID);
    }

    void removeAllSubtasks() {
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeAllSubtaskIDs();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    // Epics methods
    Collection<Epic> getAllEpic() {
        return epics.values();
    }

    Epic getEpicID(Integer id) {
        return epics.get(id);
    }

    ArrayList<Subtask> getAllSubtasksByEpicID(Integer epicID) {
        ArrayList<Subtask> subtasksInEpic = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicID().equals(epicID)) {
                subtasksInEpic.add(subtask);
            }
        }

        return subtasksInEpic;
    }

    Integer create(Epic epic) {
        Integer id = nextID++;

        epic.setId(id);
        epics.put(id, epic);

        return id;
    }

    void update(Epic epic) {
        if (Objects.equals(epic.getId(), Task.INVALID_ID)) {
            throw new IllegalArgumentException("Epic " + epic.getName() + " was not created!");
        }

        epics.put(epic.getId(), epic);
    }

    void removeEpicByID(Integer epicID) {
        if (!epics.containsKey(epicID)) {
            return;
        }

        Epic epic = epics.get(epicID);

        for (Integer subtaskID : epic.getAllSubtaskIDs()) {
            subtasks.remove(subtaskID);
        }

        epics.remove(epicID);
    }

    void removeAllEpic() {
        subtasks.clear();
        epics.clear();
    }

    private void syncEpicStatus(Integer epicID) {
        Epic epic = epics.get(epicID);
        ArrayList<Subtask> subtasksInEpic = getAllSubtasksByEpicID(epicID);

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