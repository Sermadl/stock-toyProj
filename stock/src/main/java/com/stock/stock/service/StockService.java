package com.stock.stock.service;

import com.stock.player.model.entity.Player;
import com.stock.player.repository.PlayerRepository;
import com.stock.stock.controller.dto.request.StockDescriptionUpdateRequest;
import com.stock.stock.controller.dto.request.StockAddRequest;
import com.stock.stock.controller.dto.response.StockResponse;
import com.stock.stock.model.entity.Stock;
import com.stock.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final PlayerRepository playerRepository;

    public List<StockResponse> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();

        return stocks.stream().map(
                    stock -> new StockResponse(
                            stock.getId(),
                            stock.getName(),
                            stock.getCode(),
                            stock.getPrice()
                    )
                ).toList();
    }

    public StockResponse getStock(Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));

        return new StockResponse(
                stock.getId(),
                stock.getName(),
                stock.getCode(),
                stock.getPrice()
        );
    }

    @Transactional
    public Stock addStock(StockAddRequest request, Long userId) {
        Stock stock = new Stock(
                request.getName(),
                request.getCode(),
                request.getPrice(),
                request.getDescription()
        );

        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        if (!player.getRole().isAdmin()) {
            throw new IllegalArgumentException("You are not allowed to add stock");
        }

        return stockRepository.save(stock);
    }

    @Transactional
    public Stock updateDescription(StockDescriptionUpdateRequest request, Long userId) {
        Stock stock = stockRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        if (!player.getRole().isAdmin()) {
            throw new IllegalArgumentException("You are not allowed to update stock");
        }

        stock.updateDescription(request.getDescription());

        return stock;
    }
}
