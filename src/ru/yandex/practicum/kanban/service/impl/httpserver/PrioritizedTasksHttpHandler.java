package ru.yandex.practicum.kanban.service.impl.httpserver;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.service.api.TaskManager;

import java.io.IOException;

public class PrioritizedTasksHttpHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedTasksHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
