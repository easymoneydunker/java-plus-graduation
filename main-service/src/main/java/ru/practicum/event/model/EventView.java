package ru.practicum.event.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
