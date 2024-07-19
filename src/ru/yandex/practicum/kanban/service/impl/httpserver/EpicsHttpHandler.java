package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;

import ru.yandex.practicum.kanban.service.api.TaskManager;
import ru.yandex.practicum.kanban.service.impl.DateTimeOverlapException;
import ru.yandex.practicum.kanban.service.impl.NotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EpicsHttpHandler extends BaseHttpHandler {
    private static final int SUBTASKS_STRING_INDEX_IN_REQUEST_PATH = 3;

    public EpicsHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {
        try {
            String jsonResponse;
            if (maybeId.isPresent()) {
                String path = httpExchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");

                if (pathParts.length > SUBTASKS_STRING_INDEX_IN_REQUEST_PATH &&
                        pathParts[SUBTASKS_STRING_INDEX_IN_REQUEST_PATH].equals("subtasks")) {
                    List<Subtask> subtasksInEpic = taskManager.getAllSubtasksByEpicID(maybeId.get());
                    jsonResponse = gson.toJson(subtasksInEpic);
                } else {
                    Epic epic = taskManager.getEpicByID(maybeId.get());
                    jsonResponse = gson.toJson(epic);
                }
            } else {
                List<Epic> epics = taskManager.getAllEpic();
                jsonResponse = gson.toJson(epics);
            }
            sendResponse(httpExchange, jsonResponse, HttpTaskServer.OK_200);
        } catch (NotFoundException e) {
            sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.NOT_FOUND_404);
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {
        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), HttpTaskServer.DEFAULT_CHARSET);
        Epic epic = gson.fromJson(requestBody, Epic.class);
        int rCode = HttpTaskServer.CREATED_201;
        try {
            if (maybeId.isPresent()) {
                taskManager.updateEpic(epic);
            } else {
                taskManager.createEpic(epic);
            }
        } catch (DateTimeOverlapException e) {
            rCode = HttpTaskServer.NOT_ACCEPTABLE_406;
        }
        sendResponse(httpExchange, EMPTY_RESPONSE, rCode);
    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {
        if (maybeId.isPresent()) {
            taskManager.removeEpicByID(maybeId.get());
        } else {
            taskManager.removeAllEpic();
        }
        sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.OK_200);
    }
}