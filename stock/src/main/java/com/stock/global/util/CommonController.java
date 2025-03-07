package com.stock.global.util;

import jakarta.servlet.http.HttpServletRequest;

public class CommonController {
    public Long getLoginPlayerId(HttpServletRequest httpServletRequest) {
        return (Long) httpServletRequest.getSession().getAttribute("id");
    }
}
