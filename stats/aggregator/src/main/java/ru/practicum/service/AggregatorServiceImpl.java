package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.enums.UserActionWeight;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AggregatorServiceImpl implements AggregatorService {
    private Map<Long, Map<Long, Double>> eventUserWeight;
    private Map<Long, Double> eventWeightSum;
    private Map<Long, Map<Long, Double>> twoEventsMinSum;
    private UserActionWeight userActionWeight;

    @Override
    public List<EventSimilarityAvro> aggregationUserAction(UserActionAvro action) {
        List<EventSimilarityAvro> result = new ArrayList<>();
        Double weightDiff = getDiffEventUserWeight(action);
        if (weightDiff.equals(0.0)) {
            log.info("User action weight did not change, returning an empty list");
            return result;
        }
        updateEventUserWeight(action);
        updateEventWeightSum(action, weightDiff);

        List<Long> eventIdsForCalculate = new ArrayList<>();
        for (Long id : eventUserWeight.keySet()) {
            if (!id.equals(action.getEventId())) {
                Double otherEventUserWeight = eventUserWeight.get(id).get(action.getUserId());
                if (otherEventUserWeight != null && otherEventUserWeight != 0) {
                    eventIdsForCalculate.add(id);
                }
            }
        }
        if (eventIdsForCalculate.isEmpty()) {
            log.info("No events found to calculate similarity, returning an empty list");
            return result;
        }
        log.info("Events for similarity calculation: {}", eventIdsForCalculate);

        log.info("Calculating similarity for event {}, with events: {}", action.getEventId(), eventIdsForCalculate);
        for (Long otherEventId : eventIdsForCalculate) {
            Long first = Math.min(action.getEventId(), otherEventId);
            Long second = Math.max(action.getEventId(), otherEventId);

            updateTwoEventsMinSum(first, second, action, otherEventId, weightDiff);
            EventSimilarityAvro eventSimilarity = calculateSimilarity(first, second, action);

            result.add(eventSimilarity);
        }

        log.info("Similarity calculation result: {}", result);
        return result;
    }

    private EventSimilarityAvro calculateSimilarity(Long first, Long second, UserActionAvro action) {
        double similarity = twoEventsMinSum.get(first).get(second) /
                (Math.sqrt(eventWeightSum.get(first)) * Math.sqrt(eventWeightSum.get(second)));
        log.info("Similarity between events {} and {}: {}", first, second, similarity);
        return EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(similarity)
                .setTimestamp(action.getTimestamp())
                .build();
    }

    private void updateTwoEventsMinSum(Long first, Long second, UserActionAvro action, Long otherEventId, Double weightDiff) {
        Long userId = action.getUserId();
        Long eventId = action.getEventId();
        Double eventWeight = eventUserWeight.get(eventId).getOrDefault(userId, 0.0);
        log.info("Calculating minimum sum for event {}, with event {}", eventId, otherEventId);

        Double oldEventWeight = eventWeight - weightDiff;
        log.info("Old weight: {}, for event {}, user {}", oldEventWeight, eventId, userId);

        Double otherEventWeight = eventUserWeight.get(otherEventId).getOrDefault(userId, 0.0);
        log.info("Weight: {}, for event {}, user {}", otherEventWeight, otherEventId, userId);
        if (otherEventWeight.equals(0.0)) {
            log.info("User has no action weight for event {}, skipping", otherEventId);
            return;
        }
        Map<Long, Double> map = twoEventsMinSum.get(first);
        if (map == null || map.isEmpty()) {
            twoEventsMinSum.computeIfAbsent(first, k -> new HashMap<>())
                    .put(second, Math.min(eventWeight, otherEventWeight));
            log.info("Minimum sum not yet calculated, saving new: first - {}, second - {}, sum - {}",
                    first, second, Math.min(eventWeight, otherEventWeight));
            return;
        }
        Double oldSum = map.get(second);
        log.info("Old minimum sum {}, for events {} and {}", oldSum, eventId, otherEventId);
        if (oldSum == null) {
            twoEventsMinSum.computeIfAbsent(first, k -> new HashMap<>())
                    .put(second, Math.min(eventWeight, otherEventWeight));
            log.info("Minimum sum not yet calculated, saving new: first - {}, second - {}, sum - {}",
                    first, second, Math.min(eventWeight, otherEventWeight));
            return;
        }

        if (eventWeight >= otherEventWeight) {
            log.info("eventWeight {} >= otherEventWeight {}", eventWeight, otherEventWeight);
            if (oldEventWeight >= otherEventWeight) {
                log.info("oldEventWeight {} >= otherEventWeight {}, no update needed", oldEventWeight, otherEventWeight);
                return;
            } else {
                log.info("oldEventWeight {} < otherEventWeight {}", oldEventWeight, otherEventWeight);
                oldSum += otherEventWeight - oldEventWeight;
                log.info("New minimum sum: {}", oldSum);
            }
        } else {
            log.info("eventWeight {} < otherEventWeight {}", eventWeight, otherEventWeight);
            oldSum += eventWeight - oldEventWeight;
            log.info("New minimum sum: {}", oldSum);
        }
        twoEventsMinSum.computeIfAbsent(first, k -> new HashMap<>())
                .put(second, oldSum);
        log.info("Updating minimum sum: first - {}, second - {}, sum - {}", first, second, oldSum);
    }

    private void updateEventWeightSum(UserActionAvro action, Double weightDiff) {
        Long userId = action.getUserId();
        Long eventId = action.getEventId();
        Double eventWeight = eventUserWeight.get(eventId).getOrDefault(userId, 0.0);
        log.info("Updating eventWeightSum for event: {}", eventId);
        if (weightDiff.equals(0.0)) {
            log.info("Event weight did not change, skipping update");
            return;
        }
        if (!eventWeightSum.containsKey(eventId)) {
            eventWeightSum.put(eventId, eventWeight);
            log.info("New event, sum will be equal to eventWeight: {}", eventWeight);
            return;
        }
        Double newSum = eventWeightSum.merge(eventId, weightDiff, Double::sum);
        log.info("New eventWeightSum for event {}: {}", eventId, newSum);
    }

    private Double getDiffEventUserWeight(UserActionAvro action) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();
        Double weight = getWeight(action);
        log.info("Calculating weight difference for event {}, user {}", eventId, userId);

        Map<Long, Double> oldUserWeight = eventUserWeight.get(eventId);
        if (oldUserWeight == null || oldUserWeight.isEmpty()) {
            log.info("No actions were taken by users for event {}, difference will be {}", eventId, weight);
            return weight;
        }
        Double oldWeight = oldUserWeight.get(userId);
        if (oldWeight == null || oldWeight == 0) {
            log.info("User {} had weight 0 for event {}, difference will be {}", userId, eventId, weight);
            return weight;
        }
        if (oldWeight >= weight) {
            log.info("Old weight {} >= new weight {}, difference is 0", oldWeight, weight);
            return 0.0;
        }
        Double diff = weight - oldWeight;
        log.info("New weight {} - old weight {} = {}", weight, oldWeight, diff);
        return diff;
    }

    private void updateEventUserWeight(UserActionAvro action) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();
        Double weight = getWeight(action);
        log.info("Updating eventUserWeight with weight: {}, for event {} and user {}", weight, eventId, userId);

        Map<Long, Double> oldUserWeight = eventUserWeight.get(eventId);
        if (oldUserWeight == null || oldUserWeight.isEmpty()) {
            log.info("No previous user actions for event {}, setting new weight to {}", eventId, weight);
            eventUserWeight.computeIfAbsent(eventId, k -> new HashMap<>()).put(userId, weight);
            log.info("Result: {}", eventUserWeight.get(eventId));
            return;
        }
        Double oldWeight = oldUserWeight.get(userId);
        if (oldWeight == null || oldWeight == 0) {
            log.info("User {} had weight 0 for event {}, setting new weight to {}", userId, eventId, weight);
            oldUserWeight.put(userId, weight);
            log.info("Result: {}", eventUserWeight.get(eventId));
            return;
        }
        if (oldWeight >= weight) {
            log.info("Old weight {} >= new weight {}, skipping update", oldWeight, weight);
            return;
        }
        oldUserWeight.put(userId, weight);
        log.info("Result: {}", eventUserWeight.get(eventId));
    }

    private double getWeight(UserActionAvro action) {
        return switch (action.getActionType()) {
            case VIEW -> userActionWeight.getVIEW();
            case REGISTER -> userActionWeight.getREGISTER();
            case LIKE -> userActionWeight.getLIKE();
        };
    }
}
