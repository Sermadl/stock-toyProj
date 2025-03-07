package com.stock.player.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepositRequest {
    private String password;
    private int money;
}
