package ru.netology.cloudstorage.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${spring.security.jwt.secret}")
    private String jwtSecuritySecret;
    private final TokenStore tokenStore;

    public JwtTokenFilter(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().contains("/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"No JWT token found in request headers\",\"id\":400}");
            return;
        }
        String token = header.split(" ")[1].trim();
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecuritySecret)
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            if (username != null && tokenStore.containsToken(username, token)) {
                @SuppressWarnings("unchecked")
                List<String> authorities = claims.get("authorities", List.class);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null,
                        authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (SignatureException ex) {
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid token\",\"id\":400}");
        }
    }
}
