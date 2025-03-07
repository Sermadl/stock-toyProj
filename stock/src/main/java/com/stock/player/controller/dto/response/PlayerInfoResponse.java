package com.stock.player.controller.dto.response;

import com.stock.player.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerInfoResponse {
    private String name;
    private int playerMoney;
    private Role role;
}
