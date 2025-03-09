package com.stock.order.repository;

import com.stock.order.model.entity.StockOrder;
import com.stock.order.model.entity.Type;
import com.stock.stock.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<StockOrder, Long> {
    Optional<StockOrder> findByPlayerIdAndStockId(Long playerId, Long stockId);
    List<StockOrder> findByPlayerId(Long playerId);
    List<StockOrder> findByPlayerIdAndType(Long playerId, Type type);

    // 특정 주식에 대한 대기 주문 중 매수 주문은 현재가 이하, 매도 주문은 현재가 이상인 주문 찾기
    @Query("SELECT o FROM StockOrder o WHERE o.status = 'PENDING' AND o.stock = :stock " +
            "AND ((o.type = 'BUY' AND o.targetPrice >= :currentPrice) " +
            "OR (o.type = 'SELL' AND o.targetPrice <= :currentPrice))")
    List<StockOrder> findExecutableOrders(Stock stock, int currentPrice);
}
