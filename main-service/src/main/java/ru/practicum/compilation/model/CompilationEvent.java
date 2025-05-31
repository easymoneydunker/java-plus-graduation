package ru.practicum.compilation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.model.Event;

@Entity
@Table(name = "compilation_event")
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
