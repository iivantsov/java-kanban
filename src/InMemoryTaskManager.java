import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private Integer nextID = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Tasks methods
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
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
        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task " + task.getName() + " was not created!");
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeTaskByID(Integer id) {
        tasks.remove(id);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    // Subtasks methods
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
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
        Epic epic = epics.get(epicID);
        epic.addSubtaskID(subtaskID);

        syncEpicStatus(epicID);
        return subtaskID;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Subtask " + subtask.getName() + " was not created!");
        }
        subtasks.put(subtask.getId(), subtask);
        syncEpicStatus(subtask.getEpicID());
    }

    @Override
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

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtaskIDs();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    // Epics methods
    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicByID(Integer id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksByEpicID(Integer epicID) {
        if (!epics.containsKey(epicID)) {
            throw new IllegalArgumentException("Epic ID=" + epicID + " was not created!");
        }
        ArrayList<Subtask> subtasksInEpic = new ArrayList<>();
        Epic epic = epics.get(epicID);
        for (Integer subtaskID : epic.getAllSubtaskIDs()) {
            Subtask subtask = subtasks.get(subtaskID);
            subtasksInEpic.add(subtask);
        }
        return subtasksInEpic;
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
        if (!epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Epic " + epic.getName() + " was not created!");
        }
        epics.put(epic.getId(), epic);
    }

    @Override
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

    @Override
    public void removeAllEpic() {
        subtasks.clear();
        epics.clear();
    }

    private void syncEpicStatus(Integer epicID) {
        ArrayList<Subtask> subtasksInEpic = getAllSubtasksByEpicID(epicID); // Also checks that epics contains epicID
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
}