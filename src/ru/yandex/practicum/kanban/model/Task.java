package ru.yandex.practicum.kanban.model;

import java.util.Objects;

public class Task {
    public static final Integer INVALID_ID = 0;

    protected String name;
    protected String description;
    protected Integer id;
    protected TaskStatus status;
    protected TaskTypes type;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        id = INVALID_ID;
        status = TaskStatus.NEW;
        type = TaskTypes.TASK;
    }

    public Task(Task other) {
        this.name = other.name;
        this.description = other.description;
        this.id = other.id;
        this.status = other.status;
        this.type = other.type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task task)) {
            return false;
        }

        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, type);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", type=" + getType() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", description='" + getDescription() + '\'' +
                '}';
    }
}