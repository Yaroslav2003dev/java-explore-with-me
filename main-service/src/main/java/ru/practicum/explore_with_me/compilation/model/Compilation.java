package ru.practicum.explore_with_me.compilation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean pinned;
    @NotBlank
    private String title;
    @OneToMany(
            mappedBy = "compilation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CompilationEvent> events = new ArrayList<>();
}
