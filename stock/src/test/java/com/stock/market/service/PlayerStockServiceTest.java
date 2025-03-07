package com.stock.market.service;

import com.stock.market.controller.dto.request.PurchaseStockRequest;
import com.stock.market.controller.dto.request.SellStockRequest;
import com.stock.market.controller.dto.response.MyStockResponse;
import com.stock.market.model.entity.PlayerStock;
import com.stock.market.repository.PlayerStockRepository;
import com.stock.player.model.entity.Player;
import com.stock.player.model.entity.Role;
import com.stock.player.repository.PlayerRepository;
import com.stock.stock.controller.dto.response.StockResponse;
import com.stock.stock.model.entity.Stock;
import com.stock.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PlayerStockServiceTest {
    @Autowired
    private PlayerStockService playerStockService;
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
                stock1.getPrice()
        );

        playerStock2 = new PlayerStock(
                player2,
                stock2,
                1,
                stock2.getPrice()
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
    void getPlayerStocks() {
        // given
        Long user = player2.getId();

        // when
        List<MyStockResponse> playerStocks = playerStockService.getPlayerStocks(user);

        // then
        assertEquals(2, playerStocks.size());
    }

    @Test
    void purchaseStock() {
        // given
        Long user = player2.getId();
        int stock1Cnt = playerStock1.getQuantity();
        int stock2Cnt = playerStock2.getQuantity();

        PurchaseStockRequest request1 = new PurchaseStockRequest(
                "1014",
                stock1.getId(),
                1
//                stock1.getPrice()
        );

        PurchaseStockRequest request2 = new PurchaseStockRequest(
                "1014",
                stock2.getId(),
                1
//                stock2.getPrice()
        );

        // when
        playerStockService.purchaseStock(request1, user);
        playerStockService.purchaseStock(request2, user);

        // then
        PlayerStock playerStock1 = playerStockRepository.findByPlayerIdAndStockId(user, stock1.getId())
                .orElseThrow();
        PlayerStock playerStock2 = playerStockRepository.findByPlayerIdAndStockId(user, stock2.getId())
                .orElseThrow();

        assertEquals(playerStock1.getQuantity(), stock1Cnt + request1.getQuantity());
        assertEquals(playerStock2.getQuantity(), stock2Cnt + request2.getQuantity());
    }

    @Test
    void sellStock() {
        // given
        Long user = player2.getId();
        int stock1Cnt = playerStock1.getQuantity();
        int stock2Cnt = playerStock2.getQuantity();

        SellStockRequest request1 = new SellStockRequest(
                "1014",
                stock1.getId(),
                1
//                stock1.getPrice()
        );

        SellStockRequest request2 = new SellStockRequest(
                "1014",
                stock2.getId(),
                1
//                stock2.getPrice()
        );

        // when
        playerStockService.sellStock(request1, user);
        playerStockService.sellStock(request2, user);

        // then
        PlayerStock playerStock1 = playerStockRepository.findByPlayerIdAndStockId(user, stock1.getId())
                .orElseThrow();
        PlayerStock playerStock2 = playerStockRepository.findByPlayerIdAndStockId(user, stock2.getId())
                .orElseThrow();

        assertEquals(playerStock1.getQuantity(), stock1Cnt - request1.getQuantity());
        assertEquals(playerStock2.getQuantity(), stock2Cnt - request2.getQuantity());
    }
}