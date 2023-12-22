package ru.netology.cloudstorage.security.impl;

import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.security.TokenStore;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenStoreImpl implements TokenStore {
    private final ConcurrentHashMap<String, String> tokenStorage = new ConcurrentHashMap<>();

    @Override
    public void invalidateToken(String token) {
        tokenStorage.values().removeIf(storedToken -> storedToken.equals(token));
    }

    @Override
    public void storeToken(String login, String token) {
        tokenStorage.put(login, token);
    }

    @Override
    public boolean containsToken(String login, String token) {
        return token.equals(tokenStorage.get(login));
    }
}
