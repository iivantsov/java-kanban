package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import ru.yandex.practicum.kanban.service.api.TaskManager;

import java.io.IOException;
import java.io.OutputStream;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.Optional;

public abstract class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected final String EMPTY_RESPONSE = "";
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected static final int ID_INDEX_IN_REQUEST_PATH = 2;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    protected Optional<Integer> getId(HttpExchange httpExchange) throws IOException, NumberFormatException {
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
        byte[] responseBytes = response.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.getResponseHeaders().add("Allow", "GET, POST, DELETE");
        httpExchange.sendResponseHeaders(rCode, responseBytes.length);

        try (OutputStream oStream = httpExchange.getResponseBody()) {
            oStream.write(responseBytes);
        }
    }
}