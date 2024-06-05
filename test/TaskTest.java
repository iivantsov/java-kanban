import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskTest {
    protected Task task1;
    protected Task task2;

    @BeforeEach
    protected void init() {
        task1 = new Task("NAME","DESCRIPTION");
        task2 = new Task("NAME","DESCRIPTION");
    }

    @Test
    public void givenTwoDifferentIDs_whenSetIDsToTasksWithSameNameStatusDescription_thenTasksAreNotEquals() {
        task1.setId(1);
        task2.setId(2);
        Assertions.assertNotEquals(task1, task2, "Different IDs, but are equals!");
    }

    @Test
    public void givenSingleID_whenSetIDsToTasksWithDifferentNameDescription_thenTasksAreEquals() {
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