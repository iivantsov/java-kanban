package ru.yandex.practicum.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtaskIDs;
    private LocalDateTime endDateTime;

    public Epic(String name, String description) {
        super(name, description, LocalDateTime.MAX, Duration.ZERO);
        subtaskIDs = new ArrayList<>();
        type = TaskTypes.EPIC;
        endDateTime = startDateTime;
    }

    public List<Integer> getAllSubtaskIDs() {
        return subtaskIDs;
    }

    public void addSubtaskID(Integer id) {
        if (this.id.equals(id)) {
            throw new IllegalArgumentException("Epic cannot be added to itself as Subtask!");
        }
        subtaskIDs.add(id);
    }

    public void removeSubtaskByID(Integer id) {
        subtaskIDs.remove(id);
    }

    public void removeAllSubtaskIDs() {
        subtaskIDs.clear();
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode() + subtaskIDs.hashCode());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", type=" + getType() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartDateTime() +
                ", endDateTime=" + getEndDateTime() +
                ", duration=" + getDuration().toMinutes() +
                ", description='" + getDescription() + '\'' +
                ", subtaskIDs=" + getAllSubtaskIDs() +
                '}';
    }
}