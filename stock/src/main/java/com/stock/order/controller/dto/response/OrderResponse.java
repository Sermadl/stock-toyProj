package com.stock.order.controller.dto.response;

import com.stock.order.model.entity.Status;
import com.stock.order.model.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long stockId;
    private Type type;
    private int targetPrice;
    private int quantity;
    private Status status;
}
