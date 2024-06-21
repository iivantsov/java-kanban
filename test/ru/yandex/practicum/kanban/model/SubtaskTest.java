package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubtaskTest extends TaskTest {

     @BeforeEach
     @Override
     protected void testInit() {
         task1 = new Subtask("NAME","DESCRIPTION");
         task2 = new Subtask("NAME","DESCRIPTION");
     }

     @Test
     public void testSetEpicIdThrowsIllegalArgumentExceptionIfArgumentIsSubtaskId() {
         Subtask subtask = (Subtask)task1;
         subtask.setId(1);
         assertThrows(IllegalArgumentException.class, () -> subtask.setEpicID(subtask.getId()));
     }

    @Test
    public void testFromStringProducesSubtaskCopyFromToStringOutput() {
        Subtask subtask = (Subtask)task1;
        subtask.setId(123);
        subtask.setName("Easy");
        subtask.setDescription("Say Hello World!");
        subtask.setStatus(TaskStatus.DONE);
        subtask.setEpicID(321);

        String subtaskAsString = subtask.toString();
        Subtask subtaskFromString = subtask.fromString(subtaskAsString);

        Assertions.assertEquals(subtask.getId(), subtaskFromString.getId(), "IDs are not equal!");
        Assertions.assertEquals(subtask.getType(), subtaskFromString.getType(), "Types are not equal!");
        Assertions.assertEquals(subtask.getName(), subtaskFromString.getName(), "Names are not equal!");
        Assertions.assertEquals(subtask.getStatus(), subtaskFromString.getStatus(), "Statuses are not equal!");
        Assertions.assertEquals(subtask.getDescription(), subtaskFromString.getDescription(),
                "Descriptions are not equal!");
        Assertions.assertEquals(subtask.getEpicID(), subtaskFromString.getEpicID(), "Epic IDs are not equal!");
    }
}