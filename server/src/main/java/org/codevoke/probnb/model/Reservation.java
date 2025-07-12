package org.codevoke.probnb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant startDate;
    private Instant endDate;

    @ManyToOne
    @JoinColumn(name = "guest_users_id")
    private User guest;

    @ManyToOne
    @JoinColumn(name = "host_users_id")
    private User host;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
