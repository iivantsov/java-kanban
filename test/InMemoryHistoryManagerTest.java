import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void testInit() {
        historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task", "Test Task");
        historyManager.add(task);
    }

    @Test
    void testAddSuccessfullyAddsNotNullTaskToHistory() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "History not found!");
    }

    @Test
    void testAddNotAddsNullTaskToHistory() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "History size is wrong!");
    }
}