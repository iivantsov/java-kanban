package ru.yandex.practicum.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Integer epicID;

    public Subtask(String name, String description, LocalDateTime startDateTime, Duration duration) {
        super(name, description, startDateTime, duration);
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
    public int hashCode() {
        return Objects.hash(super.hashCode() + epicID);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", type=" + getType() +
                ", name='" + getName() + '\'' +
                ", startTime=" + getStartDateTime() +
                ", endDateTime=" + getEndDateTime() +
                ", duration=" + getDuration().toMinutes() +
                ", status=" + getStatus() +
                ", description='" + getDescription() + '\'' +
                ", epicID=" + getEpicID() +
                '}';
    }
}