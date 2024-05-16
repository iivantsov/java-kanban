import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class InMemoryHistoryManagerTest {
    @Test
    void addToHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task", "Test Task");

        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "History not found!");
        assertEquals(1, history.size(), "History size is wrong!");
    }
}