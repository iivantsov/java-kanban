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
    protected void handleGetRequest(HttpExchange httpExchange) throws IOException {
        try {
            String response;
            Optional<Integer> maybeId = getId(httpExchange);
            if (maybeId.isPresent()) {
                Subtask subtask = taskManager.getSubtaskByID(maybeId.get());
                response = gson.toJson(subtask);
            } else {
                List<Subtask> subtasks = taskManager.getAllSubtasks();
                response = gson.toJson(subtasks);
            }
            sendResponse(httpExchange, response, HttpTaskServer.OK_200);
        } catch (NumberFormatException e) {
            sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.BAD_REQUEST_400);
        } catch (NotFoundException e) {
            sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.NOT_FOUND_404);
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange) throws IOException {
        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), HttpTaskServer.DEFAULT_CHARSET);
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);
        int rCode = HttpTaskServer.CREATED_201;
        try {
            Optional<Integer> maybeId = getId(httpExchange);
            if (maybeId.isPresent()) {
                taskManager.updateSubtask(subtask);
            } else {
                taskManager.createSubtask(subtask);
            }
        } catch (NumberFormatException e) {
            sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.BAD_REQUEST_400);
        } catch (DateTimeOverlapException e) {
            rCode = HttpTaskServer.NOT_ACCEPTABLE_406;
        }
        sendResponse(httpExchange, EMPTY_RESPONSE, rCode);
    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange) throws IOException {
        try {
            Optional<Integer> maybeId = getId(httpExchange);
            if (maybeId.isPresent()) {
                taskManager.removeSubtaskByID(maybeId.get());
            } else {
                taskManager.removeAllSubtasks();
            }
            sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.OK_200);
        } catch (NumberFormatException e) {
            sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.BAD_REQUEST_400);
        }
    }
}