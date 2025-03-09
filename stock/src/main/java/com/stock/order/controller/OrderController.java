package com.stock.order.controller;

import com.stock.global.util.CommonController;
import com.stock.order.controller.dto.response.OrderResponse;
import com.stock.order.controller.dto.response.OrderTypeResponse;
import com.stock.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController extends CommonController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(HttpServletRequest request) {
        return ResponseEntity.ok(
                orderService.findUserOrderHistory(getLoginPlayerId(request))
        );
    }

    @PostMapping("/{orderId}")
    public String cancelOrder(@PathVariable("orderId") Long orderId,
                              HttpServletRequest request) {
        orderService.cancelOrder(orderId, getLoginPlayerId(request));

        return "Cancel Succeed";
    }

    @GetMapping("/sell")
    public ResponseEntity<List<OrderTypeResponse>> getSellOrders(HttpServletRequest request) {
        return ResponseEntity.ok(
                orderService.findUserSellHistory(getLoginPlayerId(request))
        );
    }

    @GetMapping("/buy")
    public ResponseEntity<List<OrderTypeResponse>> getBuyOrders(HttpServletRequest request) {
        return ResponseEntity.ok(
                orderService.findUserBuyHistory(getLoginPlayerId(request))
        );
    }
}
