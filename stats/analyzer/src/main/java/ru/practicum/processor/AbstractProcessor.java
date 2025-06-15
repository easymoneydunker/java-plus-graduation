package ru.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractProcessor<K, V> implements Runnable {
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    protected final Consumer<K, V> consumer;
    protected String topicName;

    protected AbstractProcessor(Consumer<K, V> consumer, String topicName) {
        this.consumer = consumer;
        this.topicName = topicName;
    }

    protected abstract void handleRecord(V value);

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(topicName));
            while (true) {
                var records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (!records.isEmpty()) {
                    int count = 0;
                    for (ConsumerRecord<K, V> record : records) {
                        log.info("{}: Dispatching message to handler", this.getClass().getSimpleName());
                        handleRecord(record.value());
                        manageOffsets(record, count);
                        count++;
                    }
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("{}: Error while processing message", this.getClass().getSimpleName(), e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("{}: Closing consumer", this.getClass().getSimpleName());
                consumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<K, V> record, int count) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Failed committing offsets: {}", offsets, exception);
                }
            });
        }
    }
}
