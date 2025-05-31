package ru.practicum.stat.server.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.stat.server.model.EndpointHitEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EndpointHitEntityRepositoryTest {

    @Autowired
    private EndpointHitEntityRepository repository;

    @Test
    void shouldSaveAndRetrieveEndpointHit() {
        EndpointHitEntity endpointHit = new EndpointHitEntity();
        endpointHit.setApp("testApp");
        endpointHit.setUri("/test/uri");
        endpointHit.setIp("192.168.0.1");
        endpointHit.setTimestamp(LocalDateTime.now());

        EndpointHitEntity savedEntity = repository.save(endpointHit);

        Optional<EndpointHitEntity> retrievedEntity = repository.findById(savedEntity.getId());
        assertThat(retrievedEntity).isPresent();
        assertThat(retrievedEntity.get().getApp()).isEqualTo("testApp");
        assertThat(retrievedEntity.get().getUri()).isEqualTo("/test/uri");
        assertThat(retrievedEntity.get().getIp()).isEqualTo("192.168.0.1");
        assertThat(retrievedEntity.get().getTimestamp()).isEqualTo(savedEntity.getTimestamp());
    }
}
