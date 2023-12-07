package ru.netology.cloudstorage.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.security.TokenStore;
import ru.netology.cloudstorage.service.AuthService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${spring.security.jwt.secret}")
    private String jwtSecuritySecret;
    private final AuthenticationManager authenticationManager;
    private final TokenStore tokenStore;

    @Override
    public String login(String username, String password) {
        String token = "";
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username, password
        ));
        if (authentication.isAuthenticated()) {
            token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 900000))
                    .signWith(SignatureAlgorithm.HS256, jwtSecuritySecret)
                    .compact();
            tokenStore.storeToken(username, token);
        }
        return token;
    }

    @Override
    public void logout(String token) {
        tokenStore.invalidateToken(token);
    }
}
