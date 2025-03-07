package com.stock.stock.model.entity;

import com.stock.global.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private int price;
    private String description;

    public Stock(String name, String code, int price, String description) {
        this.name = name;
        this.code = code;
        this.price = price;
        this.description = description;
    }

    public void updatePrice(int price) {
        this.price = price;
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}
