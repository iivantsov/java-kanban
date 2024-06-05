import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private final List<Integer> subtaskIDs;

    Epic(String name, String description) {
        super(name, description);
        subtaskIDs = new ArrayList<>();
    }

    List<Integer> getAllSubtaskIDs() {
        return subtaskIDs;
    }

    public void addSubtaskID(Integer id) {
        if (this.id.equals(id)) {
            throw new IllegalArgumentException("Epic cannot be added to itself as a Subtask!");
        }
        subtaskIDs.add(id);
    }

    public void removeSubtaskByID(Integer id) {
        subtaskIDs.remove(id);
    }

    public void removeAllSubtaskIDs() {
        subtaskIDs.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", subtaskIDs=" + subtaskIDs +
                '}';
    }
}