package com.stock.market.repository;

import com.stock.market.model.entity.PlayerStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerStockRepository extends JpaRepository<PlayerStock, Long> {
    List<PlayerStock> findByPlayerId(Long playerId);

    Optional<PlayerStock> findByPlayerIdAndStockId(Long playerId, Long stockId);
}
