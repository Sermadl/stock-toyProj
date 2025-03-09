package com.stock.order.service;

import com.stock.market.controller.dto.request.PurchaseStockRequest;
import com.stock.market.controller.dto.request.SellStockRequest;
import com.stock.market.model.entity.PlayerStock;
import com.stock.market.repository.PlayerStockRepository;
import com.stock.market.service.PlayerStockService;
import com.stock.order.controller.dto.response.OrderResponse;
import com.stock.order.controller.dto.response.OrderTypeResponse;
import com.stock.order.model.entity.Status;
import com.stock.order.model.entity.StockOrder;
import com.stock.order.model.entity.Type;
import com.stock.order.repository.OrderRepository;
import com.stock.player.model.entity.Player;
import com.stock.player.repository.PlayerRepository;
import com.stock.stock.model.entity.Stock;
import com.stock.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PlayerRepository playerRepository;
    private final PlayerStockRepository playerStockRepository;
    private final StockRepository stockRepository;
    private final PasswordEncoder passwordEncoder;

    public List<OrderResponse> findUserOrderHistory(Long userId) {
        List<StockOrder> orders = orderRepository.findByPlayerId(userId);

        return getOrderResponse(orders);
    }

    // 주식 가격 변동에 따른 주문 체결 처리 메서드
    @Transactional
    public void processOrders(Stock stock) {
        int currentPrice = stock.getCurrentPrice();
        List<StockOrder> executableOrders = orderRepository.findExecutableOrders(stock, currentPrice);

        for (StockOrder order : executableOrders) {
            log.info("Executing order: {} for stock: {} at price: {}",
                    order.getId(), stock.getCode(), currentPrice);

            Player player = playerRepository.findById(order.getPlayer().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));

            if (order.getType() == Type.BUY) {
                player.buyStock(order.getQuantity(), order.getTargetPrice());
                if (playerStockRepository.findByPlayerIdAndStockId(player.getId(), order.getStock().getId()).isPresent()) {
                    PlayerStock playerStock = playerStockRepository.findByPlayerIdAndStockId(player.getId(), order.getStock().getId()).get();

                    playerStock.purchaseMore(order.getQuantity(), order.getTargetPrice());
                } else {
                    PlayerStock playerStock1 = new PlayerStock(
                            player,
                            stock,
                            order.getQuantity(),
                            order.getTargetPrice()
                    );

                    playerStockRepository.save(playerStock1);
                }
            } else if (order.getType() == Type.SELL) {
                player.sellStock(order.getQuantity(), order.getTargetPrice());
                PlayerStock playerStock = playerStockRepository.findByPlayerIdAndStockId(player.getId(), order.getStock().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Player not found"));

                playerStock.sellStock(order.getQuantity());
                if (playerStock.getQuantity() == 0) {
                    playerStockRepository.delete(playerStock);
                }
            }

            order.setStatus(Status.COMPLETED);
        }
    }

    @Transactional
    public StockOrder cancelOrder(Long orderId, Long userId) {
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        StockOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != Status.PENDING) {
            throw new RuntimeException("Cannot cancel order that is not pending");
        }

        order.setStatus(Status.CANCELED);

        return order;
    }

    public List<OrderTypeResponse> findUserSellHistory(Long userId) {
        List<StockOrder> orders = orderRepository.findByPlayerIdAndType(userId, Type.SELL);

        return getTypeOrderResponse(orders);
    }

    public List<OrderTypeResponse> findUserBuyHistory(Long userId) {
        List<StockOrder> orders = orderRepository.findByPlayerIdAndType(userId, Type.BUY);

        return getTypeOrderResponse(orders);
    }

    private List<OrderResponse> getOrderResponse(List<StockOrder> orders) {
        return orders.stream().map(
                order -> new OrderResponse(
                        order.getId(),
                        order.getStock().getId(),
                        order.getType(),
                        order.getTargetPrice(),
                        order.getQuantity(),
                        order.getStatus()
                )
        ).toList();
    }

    private List<OrderTypeResponse> getTypeOrderResponse(List<StockOrder> orders) {
        return orders.stream().map(
                order -> new OrderTypeResponse(
                        order.getId(),
                        order.getStock().getId(),
                        order.getTargetPrice(),
                        order.getQuantity(),
                        order.getStatus()
                )
        ).toList();
    }

    @Transactional
    public StockOrder buyStock(PurchaseStockRequest request, Long userId) {
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));

        if (!passwordEncoder.matches(request.getPassword(), player.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        StockOrder order = new StockOrder(
                player,
                stock,
                request.getPrice(),
                request.getQuantity(),
                Type.BUY
        );

        return orderRepository.save(order);
    }

    @Transactional
    public StockOrder sellStock(SellStockRequest request, Long userId) {
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));

        if (!passwordEncoder.matches(request.getPassword(), player.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        StockOrder order = new StockOrder(
                player,
                stock,
                request.getPrice(),
                request.getQuantity(),
                Type.SELL
        );

        return orderRepository.save(order);
    }
}
