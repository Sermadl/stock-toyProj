package com.stock.player.service;

import com.stock.player.controller.dto.request.DepositRequest;
import com.stock.player.controller.dto.request.LoginRequest;
import com.stock.player.controller.dto.request.WithdrawRequest;
import com.stock.player.controller.dto.request.PlayerRegisterRequest;
import com.stock.player.controller.dto.response.PlayerInfoResponse;
import com.stock.player.model.entity.Player;
import com.stock.player.repository.PlayerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Player register(PlayerRegisterRequest request) {
        Player player = new Player(
                                request.getPlayerId(),
                                passwordEncoder.encode(request.getPassword()),
                                request.getName()
                        );

        return playerRepository.save(player);
    }

    public PlayerInfoResponse getInfo(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        return new PlayerInfoResponse(
                player.getName(),
                player.getPlayerMoney(),
                player.getRole()
        );
    }

    @Transactional
    public void deposit(DepositRequest request, Long userId) {
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        if (!passwordEncoder.matches(request.getPassword(), player.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        player.deposit(request.getMoney());
    }

    @Transactional
    public void withdraw(WithdrawRequest request, Long userId) {
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        if (!passwordEncoder.matches(request.getPassword(), player.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        player.withdraw(request.getMoney());
    }

    public void login(LoginRequest request, HttpServletRequest httpServletRequest) {
        Player player = playerRepository.findByPlayerId(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        if (!passwordEncoder.matches(request.getPassword(), player.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);  // Session이 없으면 생성

        session.setAttribute("id", player.getId());
        session.setMaxInactiveInterval(1800);
    }

    public void logout(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if(session != null) {
            session.invalidate();
        } else {
            throw new RuntimeException("Session not found");
        }
    }
}
