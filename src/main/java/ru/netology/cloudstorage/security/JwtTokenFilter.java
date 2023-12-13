package ru.netology.cloudstorage.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudstorage.service.impl.StoreServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);
    @Value("${spring.security.jwt.secret}")
    private String jwtSecuritySecret;
    private final TokenStore tokenStore;

    public JwtTokenFilter(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtTokenFilter: Processing request to '{}'", request.getRequestURI());

        if (!request.getRequestURI().contains("/login")) {
            String header = request.getHeader("Authorization");
            log.debug("Authorization header: {}", header);

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.split(" ")[1].trim();
                try {
                    Claims claims = Jwts.parser()
                            .setSigningKey(jwtSecuritySecret)
                            .parseClaimsJws(token)
                            .getBody();
                    String username = claims.getSubject();

                    if (username != null && tokenStore.containsToken(username, token)) {
                        List<String> authorities = claims.get("roles", List.class);
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                username, null,
                                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.debug("Authentication set for user: {}", username);
                    }
                } catch (SignatureException ex) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Invalid token\",\"id\":400}");
                    log.error("Invalid JWT token", ex);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
