package com.stock.stock.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockResponse {
    private Long id;
    private String name;
    private String code;
    private int price;
}
