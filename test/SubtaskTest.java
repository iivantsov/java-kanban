import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubtaskTest extends TaskTest {
     @BeforeEach
     @Override
     protected void init() {
         task1 = new Subtask("NAME","DESCRIPTION");
         task2 = new Subtask("NAME","DESCRIPTION");
     }

     @Test
     public void unableToSetEpicIDAsSubtaskID() {
         Subtask subtask = (Subtask)task1;
         subtask.setId(1);

         assertThrows(IllegalArgumentException.class, () -> subtask.setEpicID(subtask.getId()));
     }
}