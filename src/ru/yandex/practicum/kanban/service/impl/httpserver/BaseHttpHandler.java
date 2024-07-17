package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.kanban.service.api.TaskManager;

public abstract class BaseHttpHandler  {
    private final TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendResponse(HttpExchange httpExchange, String response) {

    }

    protected void sendNotFound(HttpExchange httpExchange) {

    }

    protected void sendHasDateTimeOverlap(HttpExchange httpExchange) {

    }
}