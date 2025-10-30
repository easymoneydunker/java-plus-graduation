package ru.practicum.handler;

import ru.practicum.grpc.stats.actions.UserActionProto;

public interface ActionsHandler {
    void handle(UserActionProto userActionProto);
}