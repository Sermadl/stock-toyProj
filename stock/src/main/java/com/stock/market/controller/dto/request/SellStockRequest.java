package com.stock.market.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellStockRequest {
    private String password;
    private Long stockId;
    private int quantity;
//    private int price;
}
