package ru.practicum.explore_with_me.compilation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore_with_me.event.model.Event;


@Entity
@Table(name = "compilations_events")
@NoArgsConstructor
@Getter
@Setter
public class CompilationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "compilation_id")
    private Compilation compilation;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
