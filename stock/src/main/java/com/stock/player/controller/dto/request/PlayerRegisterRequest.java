package com.stock.player.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerRegisterRequest {
    private String playerId;
    private String password;
    private String name;
}
