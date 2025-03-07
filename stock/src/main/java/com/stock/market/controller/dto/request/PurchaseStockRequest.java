package com.stock.market.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PurchaseStockRequest {
    private String password;
    private Long stockId;
    private int quantity;
//    private int price;
}
