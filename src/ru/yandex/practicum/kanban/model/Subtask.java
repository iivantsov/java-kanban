package ru.yandex.practicum.kanban.model;

public class Subtask extends Task {
    private Integer epicID;

    public Subtask(String name, String description) {
        super(name, description);
        epicID = INVALID_ID;
        type = TaskTypes.SUBTASK;
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
                "id=" + getId() +
                ", type=" + getType() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", description='" + getDescription() + '\'' +
                ", epicID=" + getEpicID() +
                '}';
    }
}