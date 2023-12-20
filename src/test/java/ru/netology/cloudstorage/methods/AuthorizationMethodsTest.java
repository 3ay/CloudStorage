package ru.netology.cloudstorage.methods;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudstorage.configuration.CustomAuthenticationEntryPoint;
import ru.netology.cloudstorage.controller.AuthorizationController;
import ru.netology.cloudstorage.dto.LoginCredentials;
import ru.netology.cloudstorage.security.TokenStore;
import ru.netology.cloudstorage.service.AuthService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorizationController.class)
public class AuthorizationMethodsTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthService authService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testLoginUser() throws Exception {
        String username = "user1";
        String password = "password1";
        String fakeToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBsb2NhbGhvc3QiLCJyb2xlcyI6WyJST0xFX0FETUlOIl0sImVudGVyVGltZSI6MTcwMjQ1MzMwNzg0NiwiaWF0IjoxNzAyNDUzMzA3LCJleHAiOjE3MDI0NTY5MDd9.E8Qi-zKmLySB3bLNSuoI0dkoQ_AWm707wtYIehdX9Z0";
        when(authService.login(username, password)).thenReturn(fakeToken);
        LoginCredentials loginCredentials = new LoginCredentials();
        loginCredentials.setUsername(username);
        loginCredentials.setPassword(password);
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
        mockMvc.perform(post("/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginCredentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auth-token").value(fakeToken))
                .andDo(print());
    }
    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        String username = "user1";
        String password = "wrongPassword";
        String fakeToken = "";

        when(authService.login(username, password)).thenReturn(fakeToken);
        LoginCredentials loginCredentials = new LoginCredentials();
        loginCredentials.setUsername(username);
        loginCredentials.setPassword(password);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginCredentials)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}
