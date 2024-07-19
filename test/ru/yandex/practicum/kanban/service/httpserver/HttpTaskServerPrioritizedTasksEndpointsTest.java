package ru.yandex.practicum.kanban.service.httpserver;

import com.google.gson.Gson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.api.TaskManager;
import ru.yandex.practicum.kanban.service.impl.InMemoryTaskManager;
import ru.yandex.practicum.kanban.service.impl.httpserver.HttpTaskServer;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import java.util.List;

public class HttpTaskServerPrioritizedTasksEndpointsTest {
    private final TaskManager taskManager;
    private final HttpTaskServer taskServer;
    private final Gson gson;
    private final Duration duration = Duration.ofMinutes(30);

    public HttpTaskServerPrioritizedTasksEndpointsTest() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
    }

    @BeforeEach
    public void testInit() {
        taskServer.start();
    }

    @AfterEach
    public void testTerminate() {
        taskServer.stop();
    }

    @Test
    public void testGetReceivePrioritizedTasksFromServer() throws IOException, InterruptedException {
        LocalDateTime startDateTime = LocalDateTime.now();

        Epic newEpic1 = new Epic("Epic1", "Test Epic");
        Integer newEpic1ID = taskManager.createEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Subtask1", "Test Subtask", startDateTime, duration);
        startDateTime = startDateTime.plus(duration);
        newSubtask1.setEpicID(newEpic1ID);
        taskManager.createSubtask(newSubtask1);

        Subtask newSubtask2 = new Subtask("Subtask2", "Test Subtask", startDateTime, duration);
        newSubtask2.setEpicID(newEpic1ID);
        taskManager.createSubtask(newSubtask2);

        LocalDateTime task2Start = LocalDateTime.of(2024, Month.JUNE, 21, 17,35);
        Task task2 = new Task("Task2", "Test Task", task2Start, duration);
        taskManager.createTask(task2);

        LocalDateTime task1Start = LocalDateTime.of(2024, Month.JUNE, 9, 10,0);
        Task task1 = new Task("Task1", "Test Task", task1Start, duration);
        taskManager.createTask(task1);

        List<Task> expectedTaskList = List.of(task1, task2, newSubtask1, newSubtask2);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "prioritized");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> tasksFromServerResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertEquals(expectedTaskList, tasksFromServerResponse, "Received Tasks have wrong priority!");
        }
    }

    @Test
    public void testPostNotCompletedWithCode405() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Test Task", LocalDateTime.now(), duration);
        taskManager.createTask(task);
        String taskJsonToPost = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "prioritized");
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(taskJsonToPost))
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpTaskServer.METHOD_NOT_ALLOWED_405, response.statusCode(), "Wrong response code!");
        }
    }
}