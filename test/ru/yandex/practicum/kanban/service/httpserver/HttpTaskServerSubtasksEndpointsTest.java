package ru.yandex.practicum.kanban.service.httpserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
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

public class HttpTaskServerSubtasksEndpointsTest {
    private final TaskManager taskManager;
    private final Duration duration = Duration.ofMinutes(30);
    private Subtask subtask1;
    private Subtask subtask2;

    private final HttpTaskServer taskServer;
    private final Gson gson;

    public HttpTaskServerSubtasksEndpointsTest() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
    }

    @BeforeEach
    public void testInit() {
        Epic epic = new Epic("Epic#1", "Test Epic");
        Integer epicId =  taskManager.createEpic(epic); // Epic1 is already on Server

        LocalDateTime startDateTime = LocalDateTime.now();
        subtask1 = new Subtask("Subtask#1", "Test Subtask", startDateTime, duration);
        subtask1.setEpicID(epicId);

        startDateTime = startDateTime.plus(duration);
        subtask2 = new Subtask("Subtask#2", "Test Subtask", startDateTime, duration);
        subtask2.setEpicID(epicId);
        startDateTime = startDateTime.plus(duration);

        taskServer.start();
    }

    @AfterEach
    public void testTerminate() {
        taskServer.stop();
    }

    @Test
    public void testPostAddsSubtaskToServer() throws IOException, InterruptedException {
        String subtask1JsonToPost = gson.toJson(subtask1);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(subtask1JsonToPost))
                    .uri(uri)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();
            Subtask registeredSubtask = subtasksFromManager.getFirst();

            assertEquals(HttpTaskServer.CREATED_201, response.statusCode(),
                    "Wrong response code!");
            assertEquals(1, subtasksFromManager.size(),
                    "Wrong number of registered subtasks!");
            assertEquals(subtask1.getName(), registeredSubtask.getName(),
                    "Wrong Subtask name!" );
            assertEquals(subtask1.getDescription(), registeredSubtask.getDescription(),
                    "Wrong Subtask description!");
            assertEquals(subtask1.getStartDateTime(), registeredSubtask.getStartDateTime(),
                    "Wrong Subtask start date&time!");
        }
    }

    @Test
    public void testPostSubtaskWithDateTimeOverlapNotCompletedWithCode406() throws IOException, InterruptedException {
        Integer subtask1Id = taskManager.createTask(subtask1);
        subtask2.setStartDateTime(subtask1.getStartDateTime()); // Simulate DateTime Overlap
        String subtask2JsonToPost = gson.toJson(subtask2);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(subtask2JsonToPost))
                    .uri(uri)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpTaskServer.NOT_ACCEPTABLE_406, response.statusCode(), "Wrong response code!");
        }
    }

    @Test
    public void testGetWithRegisteredSubtaskIdReceiveSubtaskByIdFromServer() throws IOException, InterruptedException {
        Integer subtask1Id = taskManager.createSubtask(subtask1);
        Subtask subtask1FromManager = taskManager.getSubtaskByID(subtask1Id);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "subtasks/" + subtask1Id);
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "appliction/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Subtask subtask1FromServer = gson.fromJson(response.body(), Subtask.class);

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertEquals(subtask1FromManager, subtask1FromServer, "Wrong Subtask received from server!");
        }
    }

    @Test
    public void testGetWithUnregisteredSubtaskIdNotCompletedWithCode404() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "subtasks/" + Task.INVALID_ID);
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "appliction/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpTaskServer.NOT_FOUND_404, response.statusCode(), "Wrong response code!");
        }
    }

    @Test
    public void testGetWithNotIntegerSubtaskIdNotCompletedWithCode400() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "subtasks/" + "id");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "appliction/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpTaskServer.BAD_REQUEST_400, response.statusCode(), "Wrong response code!");
        }
    }

    @Test
    public void testGetReceiveAllSubtaskFromServer() throws IOException, InterruptedException{
        Integer subtask1Id = taskManager.createSubtask(subtask1);
        Integer subtask2Id = taskManager.createSubtask(subtask2);
        List<Subtask> subtasksFromManager = List.of(subtask1, subtask2);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "appliction/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Subtask> subtasksFromServer = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertEquals(subtasksFromManager, subtasksFromServer, "Not all Tasks received from server!");
        }
    }

    @Test
    public void testDeleteWithIdRemovesSubtaskByIdFromServer() throws IOException, InterruptedException {
        Integer subtask1Id = taskManager.createTask(subtask1);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "subtasks/" + subtask1Id);
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(uri)
                    .headers("Accept", "appliction/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertFalse(subtasksFromManager.contains(subtask1), "Task1 was not deleted from server!");
        }
    }

    @Test
    public void testDeleteRemovesAllSubtaskFromServer() throws IOException, InterruptedException {
        Integer subtask1Id = taskManager.createTask(subtask1);
        Integer subtask2Id = taskManager.createTask(subtask2);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(uri)
                    .headers("Accept", "appliction/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertTrue(subtasksFromManager.isEmpty(), "Some Tasks were not deleted!");
        }
    }

    @Test
    public void testPatchNotCompletedWithCode405() throws IOException, InterruptedException {
        String subtaskJsonToPatch = gson.toJson(subtask1);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(subtaskJsonToPatch))
                    .uri(uri)
                    .headers("Accept", "appliction/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpTaskServer.METHOD_NOT_ALLOWED_405, response.statusCode(), "Wrong response code!");
        }
    }
}