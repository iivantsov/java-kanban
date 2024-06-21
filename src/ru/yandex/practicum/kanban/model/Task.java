package ru.yandex.practicum.kanban.model;

import java.util.Objects;

public class Task {
    public static final Integer INVALID_ID = 0;

    protected static final String DELIMITER = ",";
    protected static final int ID_INDEX = 0;
    protected static final int TYPE_INDEX = 1;
    protected static final int NAME_INDEX = 2;
    protected static final int STATUS_INDEX = 3;
    protected static final int DESCRIPTION_INDEX = 4;

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
        return String.join(DELIMITER, id.toString(), type.toString(), name, status.toString(), description);
    }

    public Task fromString(String taskAsString) {
        String[] fields = taskAsString.split(DELIMITER);

        Integer id = Integer.parseInt(fields[ID_INDEX]);
        TaskTypes type = TaskTypes.valueOf(fields[TYPE_INDEX]);
        String name = fields[NAME_INDEX];
        TaskStatus status = TaskStatus.valueOf(fields[STATUS_INDEX]);
        String description = fields[DESCRIPTION_INDEX];

        Task task = new Task(name, description);
        task.setId(id);
        task.setStatus(status);
        task.setType(type);

        return task;
    }
}