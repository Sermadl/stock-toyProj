package com.stock.market.service;

import com.stock.market.controller.dto.response.MyStockResponse;
import com.stock.market.model.entity.PlayerStock;
import com.stock.market.repository.PlayerStockRepository;
import com.stock.stock.model.entity.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlayerStockService {
    private final PlayerStockRepository playerStockRepository;

    public List<MyStockResponse> getPlayerStocks(Long playerId) {
        List<PlayerStock> playerStocks = playerStockRepository.findByPlayerId(playerId);

        return playerStocks.stream().map(playerStock -> {
                    Stock stock = playerStock.getStock();

                    return new MyStockResponse(
                            stock.getId(),
                            stock.getName(),
                            stock.getCode(),
                            playerStock.getPrice(),
                            playerStock.getQuantity()
                    );
                }
        ).toList();
    }
}
