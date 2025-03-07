package com.stock.player.service;

import com.stock.market.model.entity.PlayerStock;
import com.stock.player.controller.dto.request.DepositRequest;
import com.stock.player.controller.dto.request.PlayerRegisterRequest;
import com.stock.player.controller.dto.request.WithdrawRequest;
import com.stock.player.model.entity.Player;
import com.stock.player.model.entity.Role;
import com.stock.player.repository.PlayerRepository;
import com.stock.stock.model.entity.Stock;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class PlayerServiceTest {
    @Autowired
    private PlayerService playerService;
    @Autowired
    private PlayerRepository playerRepository;

    private Player player1;
    private Player player2;
    private final int MONEY = 100000;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
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
                MONEY
        );

        playerRepository.save(player1);
        playerRepository.save(player2);
    }

    @Test
    void register() {
        // given
        PlayerRegisterRequest request = new PlayerRegisterRequest(
                "test",
                "test",
                "test"
        );

        // when
        Player player = playerService.register(request);

        // then
        assertEquals(player.getPlayerId(), request.getPlayerId());
    }

    @Test
    void getInfo() {
    }

    @Test
    void deposit() {
        // given
        Long user = player2.getId();
        DepositRequest request = new DepositRequest(
                "1014",
                100
        );

        // when
        playerService.deposit(request, user);

        // then
        assertEquals(player2.getPlayerMoney(), MONEY + request.getMoney());
    }

    @Test
    void withdraw() {
        // given
        Long user = player2.getId();
        WithdrawRequest request = new WithdrawRequest(
                "1014",
                100
        );

        // when
        playerService.withdraw(request, user);

        // then
        assertEquals(player2.getPlayerMoney(), MONEY - request.getMoney());
    }
}