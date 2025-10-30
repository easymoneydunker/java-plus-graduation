package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.kafka.UserActionProducer;

@Slf4j
@Service
public class CollectorServiceImpl implements CollectorService {
    private final UserActionProducer eventClient;

    @Value("${spring.kafka.topics.actions-topic}")
    private String actionsTopic;

    public CollectorServiceImpl(UserActionProducer eventClient) {
        this.eventClient = eventClient;
    }

    @Override
    public void sendUserAction(UserActionAvro userAction) {
        log.info("Preparing UserActionAvro message for sending: {}", getClass());
        log.info("Kafka topic = {}", actionsTopic);
        log.info("Kafka producer client = {}", eventClient.getProducer());
        eventClient.getProducer().send(new ProducerRecord<>(actionsTopic, userAction));
        log.info("Message sent to topic: {}", actionsTopic);
        log.info("UserActionAvro message successfully sent to Kafka: {}", userAction);
    }
}
