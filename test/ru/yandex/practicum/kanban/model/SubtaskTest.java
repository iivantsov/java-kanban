package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SubtaskTest extends TaskTest {

    @BeforeEach
    @Override
    protected void testInit() {
        Duration duration = Duration.ofMinutes(30);
        task1 = new Subtask("NAME", "DESCRIPTION", LocalDateTime.now(), duration);
        task2 = new Subtask("NAME", "DESCRIPTION", LocalDateTime.now().plus(duration), duration);
    }

    @Test
    public void testSetEpicIdThrowsIllegalArgumentExceptionIfArgumentIsSubtaskId() {
        Subtask subtask = (Subtask) task1;
        subtask.setId(1);
        assertThrows(IllegalArgumentException.class, () -> subtask.setEpicID(subtask.getId()));
    }
}