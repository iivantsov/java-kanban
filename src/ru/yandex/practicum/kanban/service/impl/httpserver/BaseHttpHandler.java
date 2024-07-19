package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.service.api.TaskManager;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected final String EMPTY_RESPONSE = "";
    protected static final int ID_INDEX_IN_REQUEST_PATH = 2;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Optional<Integer> maybeId;
        try {
            maybeId = getId(httpExchange);
        } catch (NumberFormatException e) {
            sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.BAD_REQUEST_400);
            return;
        }

        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET" -> handleGetRequest(httpExchange, maybeId);
            case "POST" -> handlePostRequest(httpExchange, maybeId);
            case "DELETE" -> handleDeleteRequest(httpExchange, maybeId);
            default -> sendResponse(httpExchange, EMPTY_RESPONSE, HttpTaskServer.METHOD_NOT_ALLOWED_405);
        }
    }

    protected abstract void handleGetRequest(HttpExchange httpExchange, Optional<Integer> maybeId)
            throws IOException;

    protected abstract void handlePostRequest(HttpExchange httpExchange, Optional<Integer> maybeId)
            throws IOException;

    protected abstract void handleDeleteRequest(HttpExchange httpExchange, Optional<Integer> maybeId)
            throws IOException;

    protected Optional<Integer> getId(HttpExchange httpExchange) throws NumberFormatException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        if (pathParts.length > ID_INDEX_IN_REQUEST_PATH) {
            int id = Integer.parseInt(pathParts[ID_INDEX_IN_REQUEST_PATH]);
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    protected void sendResponse(HttpExchange httpExchange, String response, int rCode) throws IOException {
        byte[] responseBytes = response.getBytes(HttpTaskServer.DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.getResponseHeaders().add("Allow", "GET, POST, DELETE");
        httpExchange.sendResponseHeaders(rCode, responseBytes.length);

        try (OutputStream oStream = httpExchange.getResponseBody()) {
            oStream.write(responseBytes);
        }
    }
}