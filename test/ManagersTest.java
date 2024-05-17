import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    public void givenManagersClass_whenCallGetDefaultMethods_thenObtainValidTaskAndHistoryManagerObjects()
    {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager, "Invalid TaskManager object!");
        assertNotNull(historyManager, "Invalid HistoryManager object!");
    }
}