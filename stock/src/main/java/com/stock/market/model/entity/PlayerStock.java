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

    public PlayerStock(Player player, Stock stock, int quantity, int buyPrice) {
        this.player = player;
        this.stock = stock;
        this.quantity = quantity;
        this.price = buyPrice;
    }

    public void purchaseMore(int quantity, int price) {
        int totalCost = (this.quantity* this.price) + (quantity * price);
        int totalQuantity = this.quantity + quantity;
        int newAveragePrice = (int) Math.round((double) totalCost / totalQuantity);

        this.quantity = totalQuantity;
        this.price = newAveragePrice;
    }

    public void sellStock(int quantity) {
        this.quantity -= quantity;
    }
}
