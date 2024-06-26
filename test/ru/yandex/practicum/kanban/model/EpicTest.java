package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EpicTest extends TaskTest {

    @BeforeEach
    @Override
    protected void testInit() {
        task1 = new Epic("NAME", "DESCRIPTION");
        task2 = new Epic("NAME", "DESCRIPTION");
    }

    @Test
    public void testAddSubtaskIdThrowsIllegalArgumentExceptionIfArgumentIsEpicId() {
        Epic epic = (Epic)task1;
        epic.setId(1);
        assertThrows(IllegalArgumentException.class, () -> epic.addSubtaskID(epic.getId()));
    }
}