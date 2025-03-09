package com.stock.stock.service;

import com.stock.market.model.entity.PlayerStock;
import com.stock.market.repository.PlayerStockRepository;
import com.stock.player.model.entity.Player;
import com.stock.player.model.entity.Role;
import com.stock.player.repository.PlayerRepository;
import com.stock.stock.controller.dto.request.StockAddRequest;
import com.stock.stock.controller.dto.request.StockDescriptionUpdateRequest;
import com.stock.stock.controller.dto.response.StockResponse;
import com.stock.stock.model.entity.Stock;
import com.stock.stock.repository.StockRepository;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StockServiceTest {

    @Autowired
    private StockService stockService;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private PlayerStockRepository playerStockRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Stock stock1;
    private Stock stock2;
    private Stock stock3;
    private Player player1;
    private Player player2;
    private PlayerStock playerStock1;
    private PlayerStock playerStock2;

    @BeforeEach
    void setUp() {
        stock1 = new Stock(
                "first",
                "FIR",
                100,
                "test"
        );

        stock2 = new Stock(
                "second",
                "SEC",
                150,
                "test"
        );

        stock3 = new Stock(
                "third",
                "THR",
                200,
                "test"
        );

        player1 = new Player(
                "admin",
                Role.ADMIN,
                "admin",
                "admin",
                0
        );

        player2 = new Player(
                "kse",
                Role.USER,
                passwordEncoder.encode("1014"),
                "user",
                100000
        );

        playerStock1 = new PlayerStock(
                player2,
                stock1,
                3,
                stock1.getCurrentPrice()
        );

        playerStock2 = new PlayerStock(
                player2,
                stock2,
                1,
                stock2.getCurrentPrice()
        );

        stockRepository.save(stock1);
        stockRepository.save(stock2);
        stockRepository.save(stock3);
        playerRepository.save(player1);
        playerRepository.save(player2);
        playerStockRepository.save(playerStock1);
        playerStockRepository.save(playerStock2);
    }

    @Test
    @DisplayName("모든 주식 목록 불러오기")
    void getAllStocks() {
        // given

        // when
        List<StockResponse> response = stockService.getAllStocks();

        // then
        assertNotNull(response);
    }

    @Test
    void getStock() {
        // given
        Long stockId = stock1.getId();

        // when
        StockResponse response = stockService.getStock(stockId);

        //then
        assertEquals(stock1.getName(), response.getName());
    }

    @Test
    void addStock() {
        // given
        Long admin = player1.getId();
        Long user = player2.getId();
        StockAddRequest request = new StockAddRequest(
                "test",
                "TST",
                100,
                "test"
        );

        // when
        stockService.addStock(request, admin);

        // then
        assertNotNull(stockRepository.findById(admin));
        Assertions.assertThrows(IllegalArgumentException.class, () -> stockService.addStock(request, user));
    }

    @Test
    void updateDescription() {
        // given
        Long stockId = stock1.getId();
        Long admin = player1.getId();
        Long user = player2.getId();
        StockDescriptionUpdateRequest request = new StockDescriptionUpdateRequest(
                stockId,
                "changed"
        );

        // when
        stockService.updateDescription(request, admin);
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow();

        // then
        assertEquals(stock.getDescription(), request.getDescription());
        Assertions.assertThrows(IllegalArgumentException.class, () -> stockService.updateDescription(request, user));

    }
}