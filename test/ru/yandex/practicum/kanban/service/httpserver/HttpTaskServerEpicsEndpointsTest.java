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

public class HttpTaskServerEpicsEndpointsTest {
    private final TaskManager taskManager;
    Epic workEpic;
    Subtask workSubtask1;
    Subtask workSubtask2;
    Epic educationEpic;
    Subtask educationSubtask1;

    private final HttpTaskServer taskServer;
    private final Gson gson;
    private final Duration duration = Duration.ofMinutes(30);

    public HttpTaskServerEpicsEndpointsTest() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
    }

    @BeforeEach
    public void testInit() {
        LocalDateTime startTime = LocalDateTime.now();
        workEpic = new Epic("Kanban App", "Working on Kanban App for this week");
        workSubtask1 = new Subtask("Presentation", "Make presentation", startTime, duration);
        startTime = startTime.plus(duration);
        workSubtask2 = new Subtask("Test report", "Make test report", startTime, duration);
        startTime = startTime.plus(duration);

        educationEpic = new Epic("Education", "List of education tasks for the week");
        educationSubtask1 = new Subtask("Chinese", "Translate a poem", startTime, duration);

        taskServer.start();
    }

    @AfterEach
    public void testTerminate() {
        taskServer.stop();
    }

    @Test
    public void testPostAddsEpicToServer() throws IOException, InterruptedException {
        String epicJsonToPost = gson.toJson(workEpic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(epicJsonToPost))
                    .uri(uri)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Epic> epicsFromManager = taskManager.getAllEpic();
            Epic registeredEpic = epicsFromManager.getFirst();

            assertEquals(HttpTaskServer.CREATED_201, response.statusCode(),
                    "Wrong response code!");
            assertEquals(1, epicsFromManager.size(),
                    "Wrong number of registered Epics!");
            assertEquals(workEpic.getName(), registeredEpic.getName(),
                    "Wrong Epic name!");
            assertEquals(workEpic.getDescription(), registeredEpic.getDescription(),
                    "Wrong Epic description!");
        }
    }

    @Test
    public void testGetWithRegisteredEpicIdReceiveEpicByIdFromServer() throws IOException, InterruptedException {
        Integer workEpicId = taskManager.createEpic(workEpic);
        Epic workEpicFromManager = taskManager.getEpicByID(workEpicId);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "epics/" + workEpicId);
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Epic workEpicFromServer = gson.fromJson(response.body(), Epic.class);

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertEquals(workEpicFromManager, workEpicFromServer, "Wrong Epic received from server!");
        }
    }

    @Test
    public void GetWithRegisteredEpicIdAndSubtasksInPathReceiveAllSubtasksForThisEpicFromServer()
            throws IOException, InterruptedException {
        Integer workEpicId = taskManager.createEpic(workEpic);
        workSubtask1.setEpicID(workEpicId);
        taskManager.createSubtask(workSubtask1);
        workSubtask2.setEpicID(workEpicId);
        taskManager.createSubtask(workSubtask2);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "epics/" + workEpicId + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            List<Subtask> epicSubtasksFromServer = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
            List<Subtask> epicSubtasksFromManager = taskManager.getAllSubtasksByEpicID(workEpicId);

            assertEquals(epicSubtasksFromManager, epicSubtasksFromServer,
                    "Wrong Subtasks received from server!");
        }
    }

    @Test
    public void testGetWithUnregisteredEpicIdNotCompletedWithCode404() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "epics/" + Task.INVALID_ID);
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
    public void testGetWithNotIntegerEpicIdNotCompletedWithCode400() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "epics/" + "id");
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
    public void testGetReceiveAllEpicsFromServer() throws IOException, InterruptedException {
        taskManager.createEpic(workEpic);
        taskManager.createEpic(educationEpic);
        List<Epic> epicsFromManager = List.of(workEpic, educationEpic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Epic> epicsFromServer = gson.fromJson(response.body(), new EpicListTypeToken().getType());

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertEquals(epicsFromManager, epicsFromServer, "Not all Epics received from server!");
        }
    }

    @Test
    public void testDeleteWithIdRemovesEpicByIdFromServer() throws IOException, InterruptedException {
        Integer workEpicId = taskManager.createEpic(workEpic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "epics/" + workEpicId);
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Epic> epicsFromManager = taskManager.getAllEpic();

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertFalse(epicsFromManager.contains(workEpic), "workEpic was not deleted from server!");
        }
    }

    @Test
    public void testDeleteRemovesAllEpicsFromServer() throws IOException, InterruptedException {
        taskManager.createEpic(workEpic);
        taskManager.createEpic(educationEpic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Epic> epicsFromManager = taskManager.getAllEpic();

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertTrue(epicsFromManager.isEmpty(), "Some Epics were not deleted!");
        }
    }

    @Test
    public void testOptionsNotCompletedWithCode405() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .method("OPTIONS", HttpRequest.BodyPublishers.ofString(""))
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpTaskServer.METHOD_NOT_ALLOWED_405, response.statusCode(), "Wrong response code!");
        }
    }
}