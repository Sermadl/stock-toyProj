package com.stock.market.service;

import com.stock.market.controller.dto.request.PurchaseStockRequest;
import com.stock.market.controller.dto.request.SellStockRequest;
import com.stock.market.controller.dto.response.MyStockResponse;
import com.stock.market.model.entity.PlayerStock;
import com.stock.market.repository.PlayerStockRepository;
import com.stock.player.model.entity.Player;
import com.stock.player.repository.PlayerRepository;
import com.stock.stock.model.entity.Stock;
import com.stock.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlayerStockService {
    private final PlayerStockRepository playerStockRepository;
    private final PlayerRepository playerRepository;
    private final StockRepository stockRepository;
    private final PasswordEncoder passwordEncoder;

    public List<MyStockResponse> getPlayerStocks(Long playerId) {
        List<PlayerStock> playerStocks = playerStockRepository.findByPlayerId(playerId);

        return playerStocks.stream().map(playerStock -> {
                    Stock stock = playerStock.getStock();

                    return new MyStockResponse(
                            stock.getId(),
                            stock.getName(),
                            stock.getCode(),
                            stock.getPrice(),
                            playerStock.getQuantity()
                    );
                }
        ).toList();
    }

    @Transactional
    public PlayerStock purchaseStock(PurchaseStockRequest request, Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));
        PlayerStock playerStock;

        int amount = request.getQuantity() * stock.getPrice();

        if (amount > player.getPlayerMoney()) {
            throw new IllegalArgumentException("You do not have enough money");
        }

        if(!passwordEncoder.matches(request.getPassword(), player.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        if(playerStockRepository.findByPlayerIdAndStockId(playerId, stock.getId()).isPresent()) {
            playerStock = playerStockRepository.findByPlayerIdAndStockId(playerId, stock.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));

            playerStock.purchaseMore(request.getQuantity());
        } else {
            playerStock = new PlayerStock(
                    player,
                    stock,
                    request.getQuantity()
            );

            playerStockRepository.save(playerStock);
        }

        // 잔고 감소
        player.buyStock(
                request.getQuantity(),
                stock.getPrice()
        );

        return playerStock;
    }

    @Transactional
    public void sellStock(SellStockRequest request, Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));

        if(!passwordEncoder.matches(request.getPassword(), player.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        PlayerStock playerStock = playerStockRepository.findByPlayerIdAndStockId(playerId, stock.getId())
                .orElseThrow(() -> new IllegalArgumentException("You do not have this stock"));

        if(playerStock.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock");
        }

        // 잔고 증가
        player.sellStock(
                request.getQuantity(),
                stock.getPrice()
        );

        playerStock.sellStock(request.getQuantity());
    }
}
