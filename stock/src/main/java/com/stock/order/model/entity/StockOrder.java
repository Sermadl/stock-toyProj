package com.stock.order.model.entity;

import com.stock.global.util.BaseEntity;
import com.stock.player.model.entity.Player;
import com.stock.stock.model.entity.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    private Type type;
    private int targetPrice;
    private int quantity;
    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;

    public StockOrder(Player player, Stock stock, int price, int quantity, Type type) {
        this.player = player;
        this.stock = stock;
        this.targetPrice = price;
        this.quantity = quantity;
        this.type = type;
        this.status = Status.PENDING;
    }
}
