package ru.kryuch.krtg.searcher.cron;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalTime;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        System.out.println(
                LocalTime.now() + " FILTER " + request.getRequestURI());

        chain.doFilter(request, response);
    }
}