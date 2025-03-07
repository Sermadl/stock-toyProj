package com.stock.market.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyStockResponse {
    private Long id;
    private String name;
    private String code;
    private int price;
    private int quantity;
}
