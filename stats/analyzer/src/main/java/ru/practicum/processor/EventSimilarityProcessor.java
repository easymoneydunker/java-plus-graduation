package ru.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.handler.EventSimilarityHandler;
import ru.practicum.kafka.KafkaClient;

@Slf4j
@Component
public class EventSimilarityProcessor extends AbstractProcessor<Long, EventSimilarityAvro> {
    private final EventSimilarityHandler similarityHandler;
    @Value("${analyzer.kafka.topics.events-similarity}")
    private String eventsSimilarityTopic;

    public EventSimilarityProcessor(KafkaClient kafkaClient, EventSimilarityHandler similarityHandler) {
        super(kafkaClient.getKafkaEventSimilarityConsumer(), null);
        this.similarityHandler = similarityHandler;
    }

    @Override
    protected void handleRecord(EventSimilarityAvro value) {
        similarityHandler.handleEventSimilarity(value);
    }

    @Override
    public void run() {
        super.topicName = eventsSimilarityTopic;
        super.run();
    }
}
