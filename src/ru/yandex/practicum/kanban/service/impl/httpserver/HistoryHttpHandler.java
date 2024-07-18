package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.google.gson.Gson;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.api.TaskManager;

import java.io.IOException;
import java.io.OutputStream;

import java.util.List;

public class HistoryHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public HistoryHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int rCode = HttpTaskServer.OK_200;
        String response = "";
        String method = httpExchange.getRequestMethod();

        if (method.equals("GET")) {
            List<Task> history = taskManager.getHistory();
            response = gson.toJson(history);
        } else {
            rCode = HttpTaskServer.METHOD_NOT_ALLOWED_405;
        }

        byte[] responseBytes = response.getBytes(HttpTaskServer.DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.getResponseHeaders().add("Allow", "GET");
        httpExchange.sendResponseHeaders(rCode, responseBytes.length);

        try (OutputStream oStream = httpExchange.getResponseBody()) {
            oStream.write(responseBytes);
        }
    }
}