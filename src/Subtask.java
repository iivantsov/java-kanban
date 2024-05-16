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
        if (this.id.equals(epicID)) {
            throw new IllegalArgumentException("Subtask cannot be made it's own Epic!");
        }

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