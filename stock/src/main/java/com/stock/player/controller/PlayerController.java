package com.stock.player.controller;

import com.stock.global.util.CommonController;
import com.stock.player.controller.dto.request.DepositRequest;
import com.stock.player.controller.dto.request.LoginRequest;
import com.stock.player.controller.dto.request.PlayerRegisterRequest;
import com.stock.player.controller.dto.request.WithdrawRequest;
import com.stock.player.controller.dto.response.PlayerInfoResponse;
import com.stock.player.service.PlayerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/player")
@Slf4j
@RequiredArgsConstructor
public class PlayerController extends CommonController {
    private final PlayerService playerService;

    @PostMapping("/register")
    public String register(@RequestBody PlayerRegisterRequest request) {
        playerService.register(request);

        return "Join Succeed";
    }

    // player info만 전달
    @GetMapping("/info")
    public ResponseEntity<PlayerInfoResponse> info(HttpServletRequest request) {
        log.info(String.valueOf(getLoginPlayerId(request)));

        return ResponseEntity.ok(
                playerService.getInfo(getLoginPlayerId(request))
        );
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request,
                        HttpServletRequest servletRequest) {
        playerService.login(request, servletRequest);

        return "Login Succeed";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest servletRequest) {
        playerService.logout(servletRequest);

        return "Logout Succeed";
    }

    @PostMapping("/deposit")
    public String depositMoney(@RequestBody DepositRequest request,
                               HttpServletRequest servletRequest) {

        playerService.deposit(request, getLoginPlayerId(servletRequest));

        return "Deposit Succeed";
    }

    @PostMapping("/withdraw")
    public String withdrawMoney(@RequestBody WithdrawRequest request,
                                HttpServletRequest servletRequest) {

        playerService.withdraw(request, getLoginPlayerId(servletRequest));

        return "Withdraw Succeed";
    }
}
