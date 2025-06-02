package ru.practicum.ewm.client;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

import java.net.URI;
import java.util.List;

@Component
public class StatClient {
    private final EurekaClient eurekaClient;

    public StatClient(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    private URI getStatServerUri() {
        InstanceInfo instance = eurekaClient.getNextServerFromEureka("STAT-SERVER", false);
        return URI.create(instance.getHomePageUrl());
    }

    public void hit(@Valid EndpointHit hitDto) {
        URI baseUri = getStatServerUri();
        RestClient.create(baseUri.toString())
                .post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ViewStats> getStats(String start,
                                    String end,
                                    List<String> uris,
                                    Boolean unique) {
        URI baseUri = getStatServerUri();
        return RestClient.create(baseUri.toString())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
