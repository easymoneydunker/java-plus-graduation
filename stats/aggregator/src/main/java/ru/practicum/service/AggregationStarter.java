package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.config.AppConfig;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.kafka.KafkaConfig;
import ru.practicum.kafka.producer.SimilarityEventProducer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AggregationStarter {
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final AggregatorService aggregatorService;
    private final SimilarityEventProducer similarityEventProducer;
    private final KafkaConfig kafkaConfig;
    private final AppConfig appConfig;

    public AggregationStarter(AggregatorService aggregatorService, SimilarityEventProducer eventProducer,
                              KafkaConfig kafkaConfig, AppConfig appConfig) {
        this.aggregatorService = aggregatorService;
        this.similarityEventProducer = eventProducer;
        this.kafkaConfig = kafkaConfig;
        this.appConfig = appConfig;
    }

    private static void manageOffsets(ConsumerRecord<String, UserActionAvro> record, int count,
                                      KafkaConsumer<String, UserActionAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Error while committing offsets: {}", offsets, exception);
                }
            });
        }
    }

    public void start() {
        KafkaConsumer<String, UserActionAvro> consumer = new KafkaConsumer<>(kafkaConfig.getConsumerProperties());
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(appConfig.getTopics().getActionTopic()));
            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll(appConfig.getConsumer().getConsumeAttemptsTimeoutMs());

                int count = 0;
                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();

            }
        } catch (WakeupException | InterruptedException ignores) {
        } catch (Exception e) {
            log.error("Error while processing events from sensors", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Closing consumer");
                consumer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, UserActionAvro> record) throws InterruptedException {
        log.info("topic = {}, partition = {}, offset = {}, value: {}\n",
                record.topic(), record.partition(), record.offset(), record.value());
        List<EventSimilarityAvro> result = aggregatorService.aggregationUserAction(record.value());
        log.info("aggregatorService.getSimilarities processed = {}", result);
        if (!result.isEmpty()) {
            log.info("Sending calculation results: {}", result);
            for (EventSimilarityAvro event : result) {
                similarityEventProducer.getProducer().send(new ProducerRecord<>(appConfig.getTopics().getSimilarityTopic(),
                        event));
            }
        }
    }
}