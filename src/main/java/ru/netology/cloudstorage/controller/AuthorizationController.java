package ru.netology.cloudstorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudstorage.dto.LoginCredentials;
import ru.netology.cloudstorage.dto.LoginResponseDTO;
import ru.netology.cloudstorage.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
public class AuthorizationController {
    private final AuthService authService;

        @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginCredentials credentials) {
        String token = authService.login(credentials.getLogin(),
                credentials.getPassword());
        if (!token.isEmpty()) {
            return ResponseEntity.ok(new LoginResponseDTO(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("auth-token");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            authService.logout(authorizationHeader.substring(7));
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
