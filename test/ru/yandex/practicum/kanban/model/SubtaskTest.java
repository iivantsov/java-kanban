package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SubtaskTest extends TaskTest {

    @BeforeEach
    @Override
    protected void testInit() {
        task1 = new Subtask("NAME", "DESCRIPTION", startDateTime, duration);
        task2 = new Subtask("NAME", "DESCRIPTION", startDateTime, duration);
    }

    @Test
    public void testSetEpicIdThrowsIllegalArgumentExceptionIfArgumentIsSubtaskId() {
        Subtask subtask = (Subtask) task1;
        subtask.setId(1);
        assertThrows(IllegalArgumentException.class, () -> subtask.setEpicID(subtask.getId()));
    }
}