package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.categories.model.Category;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.event.constraint.FutureAtLeastTwoHours;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "event", schema = "event")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @NotBlank
    private String title;
    private String annotation;
    private String description;

    private int confirmedRequests;
    private int participantLimit;

    @OneToMany(mappedBy = "event")
    private List<EventView> views;

    private boolean requestModeration = true;
    @NotNull
    private Boolean paid;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @FutureAtLeastTwoHours
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @ManyToMany(mappedBy = "events")
    private List<Compilation> compilations = new ArrayList<>();
}