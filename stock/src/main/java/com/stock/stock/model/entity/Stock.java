package com.stock.stock.model.entity;

import com.stock.global.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private int currentPrice;
    private String description;
    @Setter
    private int openPrice;
    @Setter
    private int previousClose;
    @Setter
    private int dayHigh;
    @Setter
    private int dayLow;
    @Setter
    private double percentChange;

    public Stock(String name, String code, int currentPrice, String description) {
        this.name = name;
        this.code = code;
        this.currentPrice = currentPrice;
        this.description = description;
        this.openPrice = currentPrice;
        this.previousClose = currentPrice;
        this.dayHigh = currentPrice;
        this.dayLow = currentPrice;
    }

    public void updatePrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

}
