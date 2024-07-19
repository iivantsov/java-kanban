package ru.yandex.practicum.kanban.service.httpserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.api.TaskManager;
import ru.yandex.practicum.kanban.service.impl.InMemoryTaskManager;
import ru.yandex.practicum.kanban.service.impl.httpserver.HttpTaskServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.List;

import java.io.IOException;

import com.google.gson.Gson;

public class HttpTaskServerTasksEndpointsTest {
    private final TaskManager taskManager;
    private final HttpTaskServer taskServer;
    private final Gson gson;
    private final Duration duration = Duration.ofMinutes(30);
    private Task task1;
    private Task task2;

    public HttpTaskServerTasksEndpointsTest() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
    }

    @BeforeEach
    public void testInit() {
        LocalDateTime startDateTime = LocalDateTime.now();
        task1 = new Task("Task1", "Test Task", startDateTime, duration);
        startDateTime = startDateTime.plus(duration);
        task2 = new Task("Task2", "Test Task", startDateTime, duration);

        taskServer.start();
    }

    @AfterEach
    public void testTerminate() {
        taskServer.stop();
    }

    @Test
    public void testPostAddsTaskToServer() throws IOException, InterruptedException {
        String taskJsonToPost = gson.toJson(task1);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(taskJsonToPost))
                    .uri(uri)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> tasksFromManager = taskManager.getAllTasks();
            Task registeredTask = tasksFromManager.getFirst();

            assertEquals(HttpTaskServer.CREATED_201, response.statusCode(),
                    "Wrong response code!");
            assertEquals(1, tasksFromManager.size(),
                    "Wrong number of registered tasks!");
            assertEquals(task1.getName(), registeredTask.getName(),
                    "Wrong Task name!" );
            assertEquals(task1.getDescription(), registeredTask.getDescription(),
                    "Wrong Task description!");
            assertEquals(task1.getStartDateTime(), registeredTask.getStartDateTime(),
                    "Wrong Task start date&time!");
        }
    }

    @Test
    public void testPostTaskWithDateTimeOverlapNotCompletedWithCode406() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        task2.setStartDateTime(task1.getStartDateTime()); // Simulate DateTime Overlap
        String taskJsonToPost = gson.toJson(task2);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(taskJsonToPost))
                    .uri(uri)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpTaskServer.NOT_ACCEPTABLE_406, response.statusCode(), "Wrong response code!");
        }
    }

    @Test
    public void testGetWithRegisteredTaskIdReceiveTaskByIdFromServer() throws IOException, InterruptedException {
        Integer task1Id = taskManager.createTask(task1);
        Task task1FromManager = taskManager.getTaskByID(task1Id);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "tasks/" + task1Id);
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task task1FromServerResponse = gson.fromJson(response.body(), Task.class);

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertEquals(task1FromManager, task1FromServerResponse, "Wrong Task received from server!");
        }
    }

    @Test
    public void testGetWithUnregisteredTaskIdNotCompletedWithCode404() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "tasks/" + Task.INVALID_ID);
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpTaskServer.NOT_FOUND_404, response.statusCode(), "Wrong response code!");
        }
    }

    @Test
    public void testGetWithNotIntegerTaskIdNotCompletedWithCode400() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "tasks/" + "id");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpTaskServer.BAD_REQUEST_400, response.statusCode(), "Wrong response code!");
        }
    }

    @Test
    public void testGetReceiveAllTaskFromServer() throws IOException, InterruptedException{
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        List<Task> tasksFromManager = List.of(task1,task2);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> tasksFromServerResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertEquals(tasksFromManager, tasksFromServerResponse, "Not all Tasks received from server!");
        }
    }

    @Test
    public void testDeleteWithIdRemovesTaskByIdFromServer() throws IOException, InterruptedException {
        Integer task1Id = taskManager.createTask(task1);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "tasks/" + task1Id);
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> tasksFromManager = taskManager.getAllTasks();

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertFalse(tasksFromManager.contains(task1), "Task1 was not deleted from server!");
        }
    }

    @Test
    public void testDeleteRemovesAllTaskFromServer() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> tasksFromManager = taskManager.getAllTasks();

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertTrue(tasksFromManager.isEmpty(), "Some Tasks were not deleted!");
        }
    }

    @Test
    public void testPutNotCompletedWithCode405() throws IOException, InterruptedException {
        String taskJsonToPut = gson.toJson(task1);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(taskJsonToPut))
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> tasksFromManager = taskManager.getAllTasks();

            assertEquals(HttpTaskServer.METHOD_NOT_ALLOWED_405, response.statusCode(), "Wrong response code!");
            assertFalse(tasksFromManager.contains(task1), "Task1 was not deleted from server!");
        }
    }
}