package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {
    protected Task task1;
    protected Task task2;

    @BeforeEach
    protected void testInit() {
        LocalDateTime startDateTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(30);
        task1 = new Task("NAME","DESCRIPTION", startDateTime, duration);
        task2 = new Task("NAME","DESCRIPTION", startDateTime, duration);
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
}