package ru.yandex.practicum.kanban.service.impl.httpserver;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.kanban.service.api.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class EpicsHttpHandler extends BaseHttpHandler {

    public EpicsHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {

    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {

    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange, Optional<Integer> maybeId) throws IOException {

    }
}
