package com.stock.market.model.entity;

import com.stock.global.util.BaseEntity;
import com.stock.player.model.entity.Player;
import com.stock.stock.model.entity.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    private int quantity;
    private int price;

    public PlayerStock(Player player, Stock stock, int quantity) {
        this.player = player;
        this.stock = stock;
        this.quantity = quantity;
        this.price = stock.getPrice();
    }

    public PlayerStock(Player player, Stock stock, int quantity, int price) {
        this.player = player;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
    }

    public void purchaseMore(int quantity) {
        this.quantity += quantity;
    }

    public void sellStock(int quantity) {
        this.quantity -= quantity;
    }
}
