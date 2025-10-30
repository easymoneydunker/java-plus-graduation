package ru.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.handler.UserActionHandler;
import ru.practicum.kafka.KafkaClient;

@Slf4j
@Component
public class UserActionProcessor extends AbstractProcessor<Long, UserActionAvro> {
    private final UserActionHandler userActionHandler;
    @Value("${analyzer.kafka.topics.user-actions}")
    private String userActionsTopic;

    public UserActionProcessor(KafkaClient kafkaClient, UserActionHandler userActionHandler) {
        super(kafkaClient.getKafkaUserActionConsumer(), null);
        this.userActionHandler = userActionHandler;
    }

    @Override
    protected void handleRecord(UserActionAvro value) {
        userActionHandler.handleUserAction(value);
    }

    @Override
    public void run() {
        super.topicName = userActionsTopic;
        super.run();
    }
}

