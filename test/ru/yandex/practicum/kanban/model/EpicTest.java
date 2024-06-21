package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.Assertions;
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

    @Test
    public void testFromStringProducesEqualEpicFromToStringOutput() {
        Epic epic = (Epic)task1;
        epic.setId(123);
        epic.setName("Easy");
        epic.setDescription("Say Hello World!");
        epic.setStatus(TaskStatus.DONE);

        String epicAsString = epic.toString();
        Epic epicFromString = epic.fromString(epicAsString);

        Assertions.assertEquals(epic.getId(), epicFromString.getId(), "IDs are not equal!");
        Assertions.assertEquals(epic.getType(), epicFromString.getType(), "Types are not equal!");
        Assertions.assertEquals(epic.getName(), epicFromString.getName(), "Names are not equal!");
        Assertions.assertEquals(epic.getStatus(), epicFromString.getStatus(), "Statuses are not equal!");
        Assertions.assertEquals(epic.getDescription(), epicFromString.getDescription(),
                "Descriptions are not equal!");
        Assertions.assertEquals(epic.getAllSubtaskIDs(), epicFromString.getAllSubtaskIDs(),
                "Subtask IDs are not equal!");
    }
}