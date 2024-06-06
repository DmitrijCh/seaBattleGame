package org.dmitrijch.repository;

import org.dmitrijch.entity.Shot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShotRepository extends JpaRepository<Shot, Long> {
    boolean existsByPlayerIdAndXAndY(Long playerId, String x, int y);
}
