package ru.practicum.model;


import jakarta.persistence.*;
import lombok.*;
import ru.practicum.dto.request.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "request", schema = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime created;

    private Long eventId;

    private Long requesterId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}