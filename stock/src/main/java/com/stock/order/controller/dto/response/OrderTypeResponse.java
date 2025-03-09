package com.stock.order.controller.dto.response;

import com.stock.order.model.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderTypeResponse {
    private Long id;
    private Long stockId;
    private int targetPrice;
    private int quantity;
    private Status status;
}
