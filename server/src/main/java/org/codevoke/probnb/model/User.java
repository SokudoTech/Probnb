package org.codevoke.probnb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;

    @Column(unique = true)
    private String username;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Auth auth;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image avatar;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private List<Room> rooms;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private List<HostReservation> hostReservations;
}
