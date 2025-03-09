package com.stock.order.model.entity;

import lombok.Getter;

@Getter
public enum Status {
    PENDING("대기"),
    CANCELED("취소됨"),
    COMPLETED("완료");

    private final String type;

    Status(String type) {
        this.type = type;
    }
}
