import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private Integer nextID = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // Tasks methods
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    public Task getTaskByID(Integer id) {
        return tasks.get(id);
    }

    public Integer create(Task task) {
        Integer id = nextID++;

        task.setId(id);
        tasks.put(id, task);

        return id;
    }

    public void update(Task task) {
        if (Objects.equals(task.getId(), Task.INVALID_ID)) {
            throw new IllegalArgumentException("Task " + task.getName() + " was not created!");
        }

        tasks.put(task.getId(), task);
    }

    public void removeTaskByID(Integer id) {
        tasks.remove(id);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    // Subtasks methods
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask getSubtaskByID(Integer id) {
        return subtasks.get(id);
    }

    public Integer create(Subtask subtask) {
        Integer epicID = subtask.getEpicID();

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

    public void update(Subtask subtask) {
        if (Objects.equals(subtask.getId(), Task.INVALID_ID)) {
            throw new IllegalArgumentException("Subtask " + subtask.getName() + " was not created!");
        }

        subtasks.put(subtask.getId(), subtask);
        syncEpicStatus(subtask.getEpicID());
    }

    public void removeSubtaskByID(Integer subtaskID) {
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

    public void removeAllSubtasks() {
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeAllSubtaskIDs();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    // Epics methods
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    public Epic getEpicID(Integer id) {
        return epics.get(id);
    }

    public ArrayList<Subtask> getAllSubtasksByEpicID(Integer epicID) {
        ArrayList<Subtask> subtasksInEpic = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicID().equals(epicID)) {
                subtasksInEpic.add(subtask);
            }
        }

        return subtasksInEpic;
    }

    public Integer create(Epic epic) {
        Integer id = nextID++;

        epic.setId(id);
        epics.put(id, epic);

        return id;
    }

    public void update(Epic epic) {
        if (Objects.equals(epic.getId(), Task.INVALID_ID)) {
            throw new IllegalArgumentException("Epic " + epic.getName() + " was not created!");
        }

        epics.put(epic.getId(), epic);
    }

    public void removeEpicByID(Integer epicID) {
        if (!epics.containsKey(epicID)) {
            return;
        }

        Epic epic = epics.get(epicID);

        for (Integer subtaskID : epic.getAllSubtaskIDs()) {
            subtasks.remove(subtaskID);
        }

        epics.remove(epicID);
    }

    public void removeAllEpic() {
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