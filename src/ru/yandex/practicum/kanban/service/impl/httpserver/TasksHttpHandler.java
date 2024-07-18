package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.sun.net.httpserver.HttpExchange;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.api.TaskManager;
import ru.yandex.practicum.kanban.service.impl.DateTimeOverlapException;
import ru.yandex.practicum.kanban.service.impl.NotFoundException;

import java.io.IOException;

import java.util.List;
import java.util.Optional;

public class TasksHttpHandler extends BaseHttpHandler {

    public TasksHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {
        try {
            String jsonResponse;
            if (maybeId.isPresent()) {
                Task task = taskManager.getTaskByID(maybeId.get());
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

    @Override
    protected void handlePostRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {
        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), HttpTaskServer.DEFAULT_CHARSET);
        Task task = gson.fromJson(requestBody, Task.class);
        int rCode = HttpTaskServer.CREATED_201;
        try {
            if (maybeId.isPresent()) {
                taskManager.updateTask(task);
            } else {
                taskManager.createTask(task);
            }
        } catch (DateTimeOverlapException e) {
            rCode = HttpTaskServer.NOT_ACCEPTABLE_406;
        }
        sendResponse(httpExchange, EMPTY_RESPONSE, rCode);
    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {
        if (maybeId.isPresent()) {
            taskManager.removeTaskByID(maybeId.get());
        } else {
            taskManager.removeAllTasks();
        }
        sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.OK_200);
    }
}