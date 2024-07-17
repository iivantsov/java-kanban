package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.service.Managers;
import ru.yandex.practicum.kanban.service.api.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHttpHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHttpHandler(taskManager));
        server.createContext("/epics", new EpicsHttpHandler(taskManager));
        server.createContext("/history", new HistoryHttpHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedTasksHttpHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}