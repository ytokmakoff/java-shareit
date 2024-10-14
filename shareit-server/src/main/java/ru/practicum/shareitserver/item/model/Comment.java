package ru.practicum.shareitserver.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    private String text;

    @OneToOne(fetch = FetchType.EAGER)
    private Item item;

    @ManyToOne
    private User user;

    private LocalDate created;
}
