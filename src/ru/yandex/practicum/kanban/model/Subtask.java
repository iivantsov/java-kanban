package ru.yandex.practicum.kanban.model;

public class Subtask extends Task {
    private static final int EPIC_ID_INDEX = 5;
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
        return String.join(DELIMITER, super.toString(), epicID.toString());
    }

    @Override
    public Subtask fromString(String subtaskAsString) {
        String[] fields = subtaskAsString.split(DELIMITER);

        Integer id = Integer.parseInt(fields[ID_INDEX]);
        TaskTypes type = TaskTypes.valueOf(fields[TYPE_INDEX]);
        String name = fields[NAME_INDEX];
        TaskStatus status = TaskStatus.valueOf(fields[STATUS_INDEX]);
        String description = fields[DESCRIPTION_INDEX];
        Integer epicID = Integer.parseInt(fields[EPIC_ID_INDEX]);

        Subtask subtask = new Subtask(name, description);
        subtask.setId(id);
        subtask.setStatus(status);
        subtask.setType(type);
        subtask.setEpicID(epicID);

        return subtask;
    }
}