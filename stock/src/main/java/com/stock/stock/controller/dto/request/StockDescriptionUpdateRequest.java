package com.stock.stock.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockDescriptionUpdateRequest {
    private Long id;
    private String description;
}
