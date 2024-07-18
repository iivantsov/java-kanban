package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.api.TaskManager;
import ru.yandex.practicum.kanban.service.impl.DateTimeOverlapException;
import ru.yandex.practicum.kanban.service.impl.NotFoundException;

import java.io.IOException;

import java.util.List;
import java.util.Optional;

public class TasksHttpHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Optional<Integer> maybeTaskId;
        try {
            maybeTaskId = getId(httpExchange);
        } catch (NumberFormatException e) {
            sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.BAD_REQUEST_400);
            return;
        }

        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET" -> handleGetRequest(httpExchange, maybeTaskId);
            case "POST" -> handlePostRequest(httpExchange, maybeTaskId);
            case "DELETE" -> handleDeleteRequest(httpExchange, maybeTaskId);
            default -> sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.METHOD_NOT_ALLOWED_405);
        }
    }

    private void handleGetRequest(HttpExchange httpExchange, Optional<Integer> maybeTaskId) throws IOException {
        try {
            String jsonResponse;
            if (maybeTaskId.isPresent()) {
                Task task = taskManager.getTaskByID(maybeTaskId.get());
                jsonResponse = gson.toJson(task);
            } else {
                List<Task> tasks = taskManager.getAllTasks();
                jsonResponse = gson.toJson(tasks);
            }
            sendResponse(httpExchange, jsonResponse, HttpTaskServer.OK_200);
        } catch (NotFoundException e) {
            sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.NOT_FOUND_404);
        }
    }

    private void handlePostRequest(HttpExchange httpExchange, Optional<Integer> maybeTaskId) throws IOException {
        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(requestBody, Task.class);
        int rCode = HttpTaskServer.CREATED_201;
        try {
            if (maybeTaskId.isPresent()) {
                taskManager.updateTask(task);
            } else {
                taskManager.createTask(task);
            }
        } catch (DateTimeOverlapException e) {
            rCode = HttpTaskServer.NOT_ACCEPTABLE_406;
        }
        sendResponse(httpExchange, EMPTY_RESPONSE, rCode);
    }

    private void handleDeleteRequest(HttpExchange httpExchange, Optional<Integer> maybeTaskId) throws IOException {
        if (maybeTaskId.isPresent()) {
            taskManager.removeTaskByID(maybeTaskId.get());
        } else {
            taskManager.removeAllTasks();
        }
        sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.OK_200);
    }
}