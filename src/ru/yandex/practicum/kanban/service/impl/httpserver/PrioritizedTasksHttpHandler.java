package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.sun.net.httpserver.HttpExchange;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.api.TaskManager;

import java.io.IOException;

import java.util.List;

public class PrioritizedTasksHttpHandler extends BaseHttpHandler {

    public PrioritizedTasksHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange) throws IOException {
        List<Task> history = taskManager.getPrioritizedTasks();
        String response = gson.toJson(history);
        sendResponse(httpExchange, response, HttpTaskServer.OK_200);
    }
}