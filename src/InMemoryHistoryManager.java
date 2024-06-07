import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> taskIdToNode = new HashMap<>();
    private Node<Task> head = null;
    private Node<Task> tail = null;
    private int size = 0;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int id = task.getId();

        if (taskIdToNode.containsKey(id)) {
            Node<Task> node = taskIdToNode.get(id);
            removeNode(node);
        }
        Node<Task> node = linkLast(task);
        taskIdToNode.put(id, node);
    }

    @Override
    public void remove(int id) {
        if (taskIdToNode.containsKey(id)) {
            Node<Task> node = taskIdToNode.get(id);
            removeNode(node);
            taskIdToNode.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private Node<Task> linkLast(Task task) {
        if (task == null) {
            return null;
        }
        Node<Task> oldTail = tail;
        Task taskCopy = new Task(task);
        Node<Task> newNode = new Node<>(oldTail, taskCopy, null);
        tail = newNode;

        if (oldTail == null) {
            head = tail;
        } else {
            oldTail.next = newNode;
        }

        ++size;
        return tail;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.next = nextNode;
            node.prev = null;
        }

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
            node.next = null;
        }

        node.element = null;
        --size;
    }

    private List<Task> getTasks() {
        if (head == null) {
            return null;
        }
        ArrayList<Task> tasks = new ArrayList<>();
        Node<Task> currNode = head;

        while (currNode != null) {
            tasks.add(currNode.element);
            currNode = currNode.next;
        }
        return tasks;
    }
}