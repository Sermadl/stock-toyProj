package com.stock.stock.service;

import com.stock.order.service.OrderService;
import com.stock.stock.controller.dto.response.StockPriceNotify;
import com.stock.stock.model.entity.Stock;
import com.stock.stock.repository.StockRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockPriceService {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final Random random = new Random();
    private final StockRepository stockRepository;
    private final OrderService orderService;

    List<Stock> stocks = new ArrayList<>();

    // 시장 상태 추적
    private LocalDateTime now;
    private boolean marketOpen = false;
    private LocalDateTime marketOpenTime;
    private LocalDateTime marketCloseTime;

    @PostConstruct
    public void initialize() {
        // 시장 설정
        initializeMarketHours();
        stocks = stockRepository.findAll();
        // 시장 상태 초기화
        updateMarketStatus();
    }

    private void initializeMarketHours() {
        // 시장 운영 시간 설정 (예: 9:00 AM - 4:00 PM, 미국 뉴욕 시간)
        now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        marketOpenTime = now.withHour(9).withMinute(0).withSecond(0);
        marketCloseTime = now.withHour(18).withMinute(0).withSecond(0);

        // 주말인 경우 시장 닫힘
        if (now.getDayOfWeek().getValue() >= 6) {
            marketOpen = false;
        } else {
            marketOpen = now.isAfter(marketOpenTime) && now.isBefore(marketCloseTime);
        }
    }

    @Scheduled(fixedRate = 1000) // 1초마다 실행
    public void updateMarketStatus() {

        // 주말인 경우 시장 닫힘
        if (now.getDayOfWeek().getValue() >= 6) {
            marketOpen = false;
            return;
        }

        // 평일 시장 시간 체크
        boolean newMarketStatus = now.isAfter(marketOpenTime) && now.isBefore(marketCloseTime);

        // 시장 상태가 변경됐을 때
        if (newMarketStatus != marketOpen) {
            marketOpen = newMarketStatus;

            // 시장이 열렸을 때 모든 주식의 오픈 가격 설정
            if (marketOpen) {
                for (Stock stock : stocks) {
                    // 갭업/갭다운 시뮬레이션 (전일 종가 대비 -2% ~ +2%)
                    double gapPercent = (random.nextDouble() * 4 - 2) / 100;
                    int previousClose = stock.getPreviousClose();
                    int openPrice = (int) Math.round(previousClose * (1 + gapPercent));

                    stock.setOpenPrice(openPrice);
                    stock.updatePrice(openPrice);
                    stock.setDayHigh(openPrice);
                    stock.setDayLow(openPrice);
                    stock.setPercentChange(0);
                }
            }
            // 시장이 닫혔을 때 종가 설정
            else {
                for (Stock stock : stocks) {
                    stock.setPreviousClose(stock.getCurrentPrice());
                }
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void updateStockPrices() {
        if (!marketOpen) {
            return;  // 시장이 닫혔으면 가격 업데이트 안 함
        }

        // 각 주식에 대해 랜덤으로 가격 변동
        for (Stock stock : stocks) {

            // 기본 변동 요소: 랜덤 (-0.1% ~ +0.1%)
            double baseChangePercent = (random.nextDouble() - 0.5) * 0.1;

            // 추가 변동 요소: 시장 상황 (-0.1% ~ +0.1%)
            double marketSentiment = (random.nextDouble() - 0.5) * 0.1;

            // 총 변동 계산
            double totalChangePercent = baseChangePercent + marketSentiment;

            int newPrice = (int) Math.round(stock.getCurrentPrice() * (1 + totalChangePercent));

            // 가격은 0보다 크게 유지
            if (newPrice <= 0) {
                newPrice = 1;
            }

            // 일중 고가/저가 업데이트
            if (newPrice > stock.getDayHigh()) {
                stock.setDayHigh(newPrice);
            }
            if (newPrice < stock.getDayLow()) {
                stock.setDayLow(newPrice);
            }

            // 변동 계산
            int changeFromPrev = newPrice - stock.getOpenPrice();
            double percentChange = Math.round((double) changeFromPrev / stock.getOpenPrice() * 1000) / 100.0;

            // 주식 정보 업데이트
            stock.setPercentChange(percentChange);
            stock.updatePrice(newPrice);

            orderService.processOrders(stock);
        }

        // 모든 클라이언트에게 업데이트된 데이터 전송
        notifyClients(
                getStockPriceNotifyList(stocks)
        );
    }

    @Scheduled(fixedRate = 10000) // 10초마다 실행
    @Transactional
    public void saveStocksToDB() {
        if (!marketOpen) {
            return;  // 시장이 닫혔으면 저장 안 함
        }

        log.info("Saving current stock prices to database...");
        stockRepository.saveAll(stocks);
        log.info("Saved {} stocks to database", stocks.size());
    }

    // 클라이언트에게 SSE로 데이터 전송
    private void notifyClients(List<StockPriceNotify> responses) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("stock-update")
                        .data(responses));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }

        // 죽은 이미터 제거
        emitters.removeAll(deadEmitters);
    }

    // 클라이언트가 SSE 연결을 요청할 때 호출되는 메서드
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        try {
            // 연결 직후 현재 데이터 전송
            emitter.send(SseEmitter.event()
                    .name("stock-update")
                    .data(
                            getStockPriceNotifyList(stocks)
                    ));
        } catch (IOException e) {
            return emitter;  // 실패해도 이미터는 반환
        }

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        emitters.add(emitter);
        return emitter;
    }

    private List<StockPriceNotify> getStockPriceNotifyList(List<Stock> request) {
        return request.stream().map(stock ->
                        new StockPriceNotify(
                                stock.getId(),
                                stock.getPreviousClose(),
                                stock.getOpenPrice(),
                                stock.getPercentChange(),
                                stock.getCurrentPrice(),
                                stock.getDayHigh(),
                                stock.getDayLow()
                        )
        ).toList();
    }
}
