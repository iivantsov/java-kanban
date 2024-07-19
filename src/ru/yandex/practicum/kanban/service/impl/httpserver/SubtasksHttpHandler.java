package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.sun.net.httpserver.HttpExchange;

import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.service.api.TaskManager;
import ru.yandex.practicum.kanban.service.impl.DateTimeOverlapException;
import ru.yandex.practicum.kanban.service.impl.NotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SubtasksHttpHandler extends BaseHttpHandler {

    public SubtasksHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {
        try {
            String jsonResponse;
            if (maybeId.isPresent()) {
                Subtask subtask = taskManager.getSubtaskByID(maybeId.get());
                jsonResponse = gson.toJson(subtask);
            } else {
                List<Subtask> subtasks = taskManager.getAllSubtasks();
                jsonResponse = gson.toJson(subtasks);
            }
            sendResponse(httpExchange, jsonResponse, HttpTaskServer.OK_200);
        } catch (NotFoundException e) {
            sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.NOT_FOUND_404);
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {
        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), HttpTaskServer.DEFAULT_CHARSET);
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);
        int rCode = HttpTaskServer.CREATED_201;
        try {
            if (maybeId.isPresent()) {
                taskManager.updateSubtask(subtask);
            } else {
                taskManager.createSubtask(subtask);
            }
        } catch (DateTimeOverlapException e) {
            rCode = HttpTaskServer.NOT_ACCEPTABLE_406;
        }
        sendResponse(httpExchange, EMPTY_RESPONSE, rCode);
    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {
        if (maybeId.isPresent()) {
            taskManager.removeSubtaskByID(maybeId.get());
        } else {
            taskManager.removeAllSubtasks();
        }
        sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.OK_200);
    }
}