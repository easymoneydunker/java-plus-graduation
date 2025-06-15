package ru.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.actions.ActionTypeProto;
import ru.practicum.grpc.stats.actions.UserActionProto;
import ru.practicum.service.CollectorService;

import java.time.Instant;

@Slf4j
@Component
public class UserActionHandler implements ActionsHandler {
    CollectorService collectorService;

    public UserActionHandler(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    @Override
    public void handle(UserActionProto userActionProto) {
        log.info("UserActionHandler started working");

        log.info("Input received: {}", userActionProto.toString());
        UserActionAvro userActionAvro = new UserActionAvro();

        userActionAvro.setUserId(userActionProto.getUserId());
        log.info("Set userId = {}", userActionAvro.getUserId());

        userActionAvro.setActionType(getActionType(userActionProto.getActionType()));
        log.info("Set actionType = {}", userActionAvro.getActionType());

        userActionAvro.setEventId(userActionProto.getEventId());
        log.info("Set eventId = {}", userActionAvro.getEventId());

        userActionAvro.setTimestamp(Instant.ofEpochSecond(
                userActionProto.getTimestamp().getSeconds(),
                userActionProto.getTimestamp().getNanos()));
        log.info("Set timestamp = {}", userActionAvro.getTimestamp());

        collectorService.sendUserAction(userActionAvro);
    }

    private ActionTypeAvro getActionType(ActionTypeProto actionTypeProto) {
        if (actionTypeProto.equals(ActionTypeProto.ACTION_LIKE)) {
            return ActionTypeAvro.LIKE;
        }
        if (actionTypeProto.equals(ActionTypeProto.ACTION_REGISTER)) {
            return ActionTypeAvro.REGISTER;
        }
        if (actionTypeProto.equals(ActionTypeProto.ACTION_VIEW)) {
            return ActionTypeAvro.VIEW;
        }
        return null;
    }
}