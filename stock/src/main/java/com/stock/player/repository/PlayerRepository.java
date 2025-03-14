package com.stock.player.repository;

import com.stock.player.model.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByPlayerId(String playerId);
}
