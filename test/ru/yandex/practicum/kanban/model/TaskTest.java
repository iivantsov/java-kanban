package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskTest {
    protected Task task1;
    protected Task task2;

    @BeforeEach
    protected void testInit() {
        task1 = new Task("NAME","DESCRIPTION");
        task2 = new Task("NAME","DESCRIPTION");
    }

    @Test
    public void testSetTwoDifferentIDsToTasksWithSameNameStatusDescriptionResultsInTasksAreNotEquals() {
        task1.setId(1);
        task2.setId(2);
        Assertions.assertNotEquals(task1, task2, "Different IDs, but are equals!");
    }

    @Test
    public void testSetSingleIDToTasksWithDifferentNameDescriptionResultsInTasksAreEquals() {
        int id = 777;
        task1.setId(id);
        task1.setName("Easy");
        task1.setDescription("Say Hello World!");
        task2.setId(id);
        task2.setName("Hard");
        task2.setDescription("Say Freundschaftsbeziehungen!");

        Assertions.assertEquals(task1, task2, "Same IDs, but are not equals!");
    }

    @Test
    public void testFromStringProducesEqualTaskFromToStringOutput() {
        task1.setId(123);
        task1.setName("Easy");
        task1.setDescription("Say Hello World!");
        task1.setStatus(TaskStatus.DONE);

        String task1AsString = task1.toString();
        Task task1FromString = task1.fromString(task1AsString);

        Assertions.assertEquals(task1.getId(), task1FromString.getId(), "IDs are not equal!");
        Assertions.assertEquals(task1.getType(), task1FromString.getType(), "Types are not equal!");
        Assertions.assertEquals(task1.getName(), task1FromString.getName(), "Names are not equal!");
        Assertions.assertEquals(task1.getStatus(), task1FromString.getStatus(), "Statuses are not equal!");
        Assertions.assertEquals(task1.getDescription(), task1FromString.getDescription(),
                "Descriptions are not equal!");
    }
}