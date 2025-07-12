package org.codevoke.probnb.repository;

import org.codevoke.probnb.model.HostReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostReservationRepository extends JpaRepository<HostReservation, Long> {
    List<HostReservation> findByRoomId(Long roomId);
} 