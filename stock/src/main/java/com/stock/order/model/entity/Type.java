package com.stock.order.model.entity;

import lombok.Getter;

@Getter
public enum Type {
    BUY("구매"),
    SELL("판매");

    private final String type;

    Type(String type) {
        this.type = type;
    }
}
