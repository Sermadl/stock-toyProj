package com.stock.stock.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockPriceNotify {
    private Long id;
    private int previousClose;
    private int openPrice;
    private double percentChange;
    private int currentPrice;
    private int dayHigh;
    private int dayLow;
}
