package ru.yandex.practicum.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    public static final Integer INVALID_ID = 0;

    protected String name;
    protected String description;
    protected Integer id;
    protected TaskStatus status;
    protected TaskTypes type;

    protected LocalDateTime startDateTime;
    protected Duration duration;

    public Task(String name, String description, LocalDateTime startDateTime, Duration duration) {
        this.name = name;
        this.description = description;
        id = INVALID_ID;
        status = TaskStatus.NEW;
        type = TaskTypes.TASK;
        this.startDateTime = startDateTime;
        this.duration = duration;
    }

    public Task(Task other) {
        this.name = other.name;
        this.description = other.description;
        this.id = other.id;
        this.status = other.status;
        this.type = other.type;
        this.startDateTime = other.startDateTime;
        this.duration = other.duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskTypes getType() {
        return type;
    }

    public void setType(TaskTypes type) {
        this.type = type;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndDateTime() {
        return startDateTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", type=" + getType() +
                ", name='" + getName() + '\'' +
                ", startDateTime=" + getStartDateTime() +
                ", duration=" + getDuration().toMinutes() +
                ", status=" + getStatus() +
                ", description='" + getDescription() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task task)) {
            return false;
        }

        return Objects.equals(getId(), task.getId())
                && Objects.equals(getStartDateTime(), task.getStartDateTime())
                && Objects.equals(getDuration(), task.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(),
                getDescription(),
                getId(),
                getStatus(),
                getType(),
                getStartDateTime(),
                getDuration());
    }
}