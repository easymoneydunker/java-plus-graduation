package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionHandler {
    private final UserActionRepository actionRepository;
    private final UserActionMapper actionMapper;

    public void handleUserAction(UserActionAvro avro) {
        log.info("Entered handleUserAction method");
        UserAction action = actionMapper.mapToUserAction(avro);
        log.info("Starting validation check");

        if (!actionRepository.existsByEventIdAndUserId(action.getEventId(), action.getUserId())) {
            action = actionRepository.save(action);
            log.info("Saving new user action: {}", action);
        } else {
            UserAction oldAction = actionRepository
                    .findByEventIdAndUserId(action.getEventId(), action.getUserId()).get();
            log.info("Fetched existing user action from DB: {}", oldAction);
            if (action.getWeight() > oldAction.getWeight()) {
                oldAction.setWeight(action.getWeight());
                oldAction.setTimestamp(action.getTimestamp());
                oldAction = actionRepository.save(oldAction);
                log.info("Action weight increased, updating DB record: {}", oldAction);
            } else {
                log.info("Action weight didn't increase, skipping update");
            }
        }
    }
}
