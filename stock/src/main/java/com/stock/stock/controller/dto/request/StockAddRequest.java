package com.stock.stock.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockAddRequest {
    private String name;
    private String code;
    private int price;
    private String description;
}
