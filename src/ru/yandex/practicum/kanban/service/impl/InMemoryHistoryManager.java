package ru.yandex.practicum.kanban.service.impl;

import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.api.HistoryManager;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private class TaskLinkedList {
        private Node head = null;
        private Node tail = null;
        private int size = 0;

        private Node linkLast(Task task) {
            if (task == null) {
                return null;
            }
            Node oldTail = tail;
            Task taskCopy = new Task(task);
            Node newNode = new Node(oldTail, taskCopy, null);
            tail = newNode;

            if (oldTail == null) {
                head = tail;
            } else {
                oldTail.setNext(newNode);
            }

            ++size;
            return tail;
        }

        private void removeNode(Node node) {
            if (node == null) {
                return;
            }
            Node prev = node.getPrev();
            Node next = node.getNext();

            if (prev == null) {
                head = next;
            } else {
                prev.setNext(next);
                node.setPrev(null);
            }

            if (next == null) {
                tail = prev;
            } else {
                next.setPrev(prev);
                node.setNext(null);
            }

            node.setTask(null);
            --size;
        }

        private List<Task> getTasks() {
            if (head == null) {
                return null;
            }
            ArrayList<Task> tasks = new ArrayList<>();
            Node currNode = head;

            while (currNode != null) {
                tasks.add(currNode.getTask());
                currNode = currNode.getNext();
            }
            return tasks;
        }
    }

    private final TaskLinkedList taskList = new TaskLinkedList();
    private final HashMap<Integer, Node> taskIdToNode = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int id = task.getId();

        if (taskIdToNode.containsKey(id)) {
            Node node = taskIdToNode.get(id);
            taskList.removeNode(node);
        }

        Node node = taskList.linkLast(task);
        taskIdToNode.put(id, node);
    }

    @Override
    public void remove(int id) {
        if (taskIdToNode.containsKey(id)) {
            Node node = taskIdToNode.get(id);
            taskList.removeNode(node);
            taskIdToNode.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskList.getTasks();
    }
}