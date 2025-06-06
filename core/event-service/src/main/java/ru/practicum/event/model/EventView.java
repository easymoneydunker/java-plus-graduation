package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "views")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String ip;
    @ManyToOne
    Event event;
}
