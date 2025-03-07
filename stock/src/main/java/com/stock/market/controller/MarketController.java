package com.stock.market.controller;

import com.stock.global.util.CommonController;
import com.stock.market.controller.dto.request.PurchaseStockRequest;
import com.stock.market.controller.dto.request.SellStockRequest;
import com.stock.market.controller.dto.response.MyStockResponse;
import com.stock.market.service.PlayerStockService;
import com.stock.stock.controller.dto.response.StockResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/market")
@Slf4j
@RequiredArgsConstructor
public class MarketController extends CommonController {
    private final PlayerStockService playerStockService;

    // 주식 구매
    @PostMapping("/purchase")
    public String purchaseStock(@RequestBody PurchaseStockRequest request,
                                HttpServletRequest servletRequest) {
        playerStockService.purchaseStock(request, getLoginPlayerId(servletRequest));

        return "Purchase Stock Succeed";
    }

    // 주식 판매
    @PostMapping("/sell")
    public String sellStock(@RequestBody SellStockRequest request,
                            HttpServletRequest servletRequest) {
        playerStockService.sellStock(request, getLoginPlayerId(servletRequest));

        return "Sell Stock Succeed";
    }

    // 구매한 주식 목록 보기
    @GetMapping("/my-stocks")
    public ResponseEntity<List<MyStockResponse>> getMyStocks(HttpServletRequest servletRequest) {
        return ResponseEntity.ok(
                playerStockService.getPlayerStocks(getLoginPlayerId(servletRequest))
        );
    }
}
