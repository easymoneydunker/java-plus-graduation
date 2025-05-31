package ru.practicum.event.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.categories.model.Category;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.constraint.FutureAtLeastTwoHours;
import ru.practicum.user.model.User;

@Getter
@Setter
@Entity
@Table(name = "event")
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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