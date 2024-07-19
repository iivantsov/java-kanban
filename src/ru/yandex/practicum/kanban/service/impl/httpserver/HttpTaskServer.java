package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.Managers;
import ru.yandex.practicum.kanban.service.api.TaskManager;

import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    public static final String URL = "http://localhost:" + PORT + "/";
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final int OK_200 = 200;
    public static final int CREATED_201 = 201;
    public static final int BAD_REQUEST_400 = 400;
    public static final int NOT_FOUND_404 = 404;
    public static final int METHOD_NOT_ALLOWED_405 = 405;
    public static final int NOT_ACCEPTABLE_406 = 406;

    private final HttpServer server;
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

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

        // Test
        LocalDateTime startDateTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task("Task1", "Test Task", startDateTime, duration);
        startDateTime = startDateTime.plus(duration);
        Task task2 = new Task("Task2", "Test Task", startDateTime, duration);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static Gson getGson() {
        return gson;
    }
}