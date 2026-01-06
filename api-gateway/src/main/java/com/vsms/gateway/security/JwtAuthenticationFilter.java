package com.vsms.gateway.security;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

 
    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/auth/",
        "/api/auth",
        "/internal/",
        "/api/users/"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath) || path.equals(publicPath.replace("/", ""))) {
                return chain.filter(exchange);
            }
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            
            
            if (role == null) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

           
            if (!isAuthorized(path, role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Role", role)
                            .header("X-User-Email", claims.getSubject())
                            .build())
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isAuthorized(String path, String role) {
        
     
        if (path.startsWith("/api/admin")) {
            return "ADMIN".equals(role);
        }
        
     
        if (path.startsWith("/api/manager")) {
            return "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        
       
        if (path.startsWith("/api/technician")) {
            return "TECHNICIAN".equals(role);
        }
        
       
        if (path.startsWith("/api/customer")) {
            return "CUSTOMER".equals(role) || "ADMIN".equals(role);
        }
     
        if (path.startsWith("/api/billing/admin")) {
            return "ADMIN".equals(role);
        }
        if (path.startsWith("/api/billing/customer")) {
            return "CUSTOMER".equals(role);
        }
        if (path.startsWith("/api/billing")) {
            return "MANAGER".equals(role) || "ADMIN".equals(role) || "CUSTOMER".equals(role);
        }
        
        if (path.startsWith("/api/inventory/requests") && !path.contains("/approve")) {
            return "TECHNICIAN".equals(role) || "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        

        if (path.equals("/api/inventory/parts") || path.startsWith("/api/inventory/parts/")) {
            return "TECHNICIAN".equals(role) || "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        
        if (path.startsWith("/api/inventory")) {
            return "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        
      
        if (path.startsWith("/api/bays")) {
            return "MANAGER".equals(role) || "ADMIN".equals(role);
        }
  
        if (path.startsWith("/api/notifications")) {
            return true;
        }
        
        if (path.startsWith("/api/profile")) {
            return true;
        }

        return true;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
