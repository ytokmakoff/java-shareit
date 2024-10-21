package ru.practicum.shareitserver.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date_time")
    private LocalDateTime start;
    @Column(name = "end_date_time")
    private LocalDateTime end;
    @OneToOne
    @JoinColumn(name = "item", referencedColumnName = "id")
    private Item item;
    @OneToOne
    @JoinColumn(name = "booker", referencedColumnName = "id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}