package ru.practicum.compilation.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.event.model.Event;

@Entity
@Table(name = "compilation_event", schema = "event")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "compilation_id", nullable = false)
    private Compilation compilationId;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event eventId;
}
