package ru.yandex.practicum.kanban.model;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private static final int NUMBER_OF_SUBTASKS_INDEX = 5;
    private final List<Integer> subtaskIDs;

    public Epic(String name, String description) {
        super(name, description);
        subtaskIDs = new ArrayList<>();
        type = TaskTypes.EPIC;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());

        builder.append(DELIMITER).append(subtaskIDs.size());
        for (Integer id : subtaskIDs) {
            builder.append(DELIMITER).append(id.toString());
        }

        return builder.toString();
    }

    @Override
    public Epic fromString(String epicAsString) {
        String[] fields = epicAsString.split(DELIMITER);

        Integer id = Integer.parseInt(fields[ID_INDEX]);
        TaskTypes type = TaskTypes.valueOf(fields[TYPE_INDEX]);
        String name = fields[NAME_INDEX];
        TaskStatus status = TaskStatus.valueOf(fields[STATUS_INDEX]);
        String description = fields[DESCRIPTION_INDEX];
        int numberOfSubtasks = Integer.parseInt(fields[NUMBER_OF_SUBTASKS_INDEX]);

        Epic epic = new Epic(name, description);
        epic.setId(id);
        epic.setStatus(status);
        epic.setType(type);

        for (int subtaskIndex = 0; subtaskIndex < numberOfSubtasks; ++subtaskIndex)
        {
            int parseIndex = subtaskIndex + NUMBER_OF_SUBTASKS_INDEX;
            Integer subtaskID = Integer.parseInt(fields[parseIndex]);
            epic.addSubtaskID(subtaskID);
        }

        return epic;
    }
}