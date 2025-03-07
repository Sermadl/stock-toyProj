package com.stock.player.model.entity;

import com.stock.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Player extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String playerId;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String password;
    private String name;
    private int playerMoney;

    public Player(String playerId, String password, String name) {
        this.playerId = playerId;
        this.password = password;
        this.name = name;
        this.role = Role.USER;
        this.playerMoney = 0;
    }

    public Player(String playerId, Role role, String password, String name, int playerMoney) {
        this.playerId = playerId;
        this.password = password;
        this.name = name;
        this.role = role;
        this.playerMoney = playerMoney;
    }

    public void deposit(int money) {
        playerMoney += money;
    }

    public void withdraw(int money) {
        playerMoney -= money;
    }

    public void buyStock(int quantity, int price) {
        playerMoney -= quantity * price;
    }

    public void sellStock(int quantity, int price) {
        playerMoney += quantity * price;
    }
}
