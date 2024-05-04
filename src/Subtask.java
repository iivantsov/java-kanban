public class Subtask extends Task {
    private Integer epicID;

    Subtask(String name, String description) {
        super(name, description);
        epicID = INVALID_ID;
    }

    public Integer getEpicID() {
        return epicID;
    }

    public void setEpicID(Integer epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", epicID=" + epicID +
                '}';
    }
}