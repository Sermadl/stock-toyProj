package com.stock.stock.controller;

import com.stock.global.util.CommonController;
import com.stock.stock.controller.dto.request.StockDescriptionUpdateRequest;
import com.stock.stock.controller.dto.request.StockAddRequest;
import com.stock.stock.controller.dto.response.StockResponse;
import com.stock.stock.service.StockService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
@Slf4j
@RequiredArgsConstructor
public class StockController extends CommonController {
    private final StockService stockService;

    // 모든 주식 목록 불러오기
    @GetMapping("/all")
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        return ResponseEntity.ok(
                stockService.getAllStocks()
        );
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable("stockId") Long stockId) {
        return ResponseEntity.ok(
                stockService.getStock(stockId)
        );
    }

    @PostMapping
    public String addStock(@RequestBody StockAddRequest request,
                                HttpServletRequest servletRequest) {
        log.info("name : {}", request.getName());
        log.info("code : {}", request.getCode());
        log.info("price : {}", request.getPrice());
        log.info("description : {}", request.getDescription());

        stockService.addStock(request, getLoginPlayerId(servletRequest));

        return "Add Stock Succeed";
    }

    @PostMapping("/description-update")
    public String updateDescription(@RequestBody StockDescriptionUpdateRequest request,
                                    HttpServletRequest servletRequest) {
        stockService.updateDescription(request, getLoginPlayerId(servletRequest));

        return "";
    }
}
