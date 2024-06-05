package org.dmitrijch.repository;

import org.dmitrijch.entity.Player;
import org.dmitrijch.entity.Ship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipRepository extends JpaRepository<Ship, Long> {
    List<Ship> findByPlayer(Player player);

    List<Ship> findByPlayerId(Long playerId);
}
