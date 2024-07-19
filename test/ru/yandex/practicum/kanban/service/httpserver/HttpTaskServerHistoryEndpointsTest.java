package ru.yandex.practicum.kanban.service.httpserver;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerHistoryEndpointsTest {
    private final TaskManager taskManager;
    private final HttpTaskServer taskServer;
    private final Gson gson;
    private final Duration duration = Duration.ofMinutes(30);

    public HttpTaskServerHistoryEndpointsTest() throws IOException {
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
        LocalDateTime startTime = LocalDateTime.now();

        Task hobbyTask = new Task("Violin", "Try to play Vivaldi", startTime, duration);
        Integer hobbyTaskID = taskManager.createTask(hobbyTask);
        startTime = startTime.plus(duration);

        Task houseTask = new Task("Cleaning", "Wash the floors at home", startTime, duration);
        Integer houseTaskID = taskManager.createTask(houseTask);
        startTime = startTime.plus(duration);

        Epic educationEpic = new Epic("Education", "List of education tasks for the week");
        Integer educationEpicID = taskManager.createEpic(educationEpic);

        Subtask educationSubtask1 = new Subtask("Chinese", "Translate the poem", startTime, duration);
        educationSubtask1.setEpicID(educationEpicID);
        Integer educationSubtask1ID = taskManager.createSubtask(educationSubtask1);

        taskManager.getTaskByID(hobbyTaskID);
        taskManager.getEpicByID(educationEpicID);
        taskManager.getTaskByID(houseTaskID);
        taskManager.getSubtaskByID(educationSubtask1ID);

        List<Task> expectedHistory = List.of(hobbyTask, educationEpic, houseTask, educationSubtask1);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "history");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> tasksFromServerResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());

            assertEquals(HttpTaskServer.OK_200, response.statusCode(), "Wrong response code!");
            assertEquals(expectedHistory, tasksFromServerResponse, "Received Tasks have wrong priority!");
        }
    }

    @Test
    public void testDeleteNotCompletedWithCode405() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(HttpTaskServer.URL + "history");
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpTaskServer.METHOD_NOT_ALLOWED_405, response.statusCode(), "Wrong response code!");
        }
    }
}