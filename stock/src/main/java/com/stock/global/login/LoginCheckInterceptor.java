package com.stock.global.login;

import com.stock.player.repository.PlayerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {
    private final PlayerRepository playerRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();

        if(session == null) {
            throw new RuntimeException("You are not logged in");
        }

        playerRepository.findById((Long) session.getAttribute("id"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
