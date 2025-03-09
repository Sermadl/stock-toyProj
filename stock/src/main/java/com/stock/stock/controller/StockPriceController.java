package com.stock.stock.controller;

import com.stock.stock.service.StockPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
@RequestMapping("/stock/price")
@RequiredArgsConstructor
public class StockPriceController {
    private final StockPriceService stockPriceService;

    // SSE 엔드포인트 - 실시간 업데이트
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStockPrices() {
        return stockPriceService.subscribe();
    }
}
