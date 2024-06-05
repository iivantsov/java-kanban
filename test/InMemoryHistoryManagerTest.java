import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class InMemoryHistoryManagerTest {

    @Test
    void givenOneValidAndOneNullTask_whenAddToHistory_thenProduceNotNullHistorySizeOfOne() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task", "Test Task");
        historyManager.add(task);
        historyManager.add(null); // Attempt to get Task by unregistered ID
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "History not found!");
        assertEquals(1, history.size(), "History size is wrong!");
    }
}