import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIDs;

    Epic(String name, String description) {
        super(name, description);
        subtaskIDs = new ArrayList<>();
    }

    ArrayList<Integer> getAllSubtaskIDs() {
        return subtaskIDs;
    }

    public void addSubtaskID(Integer id) {
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
