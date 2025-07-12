package org.codevoke.probnb.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subtitle;
    private String description;
    private Long price;
    private Integer roomsCount;
    private String location;
    private String roomType;
    private Double rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User host;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<RoomImage> images;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<HostReservation> hostReservations;
}