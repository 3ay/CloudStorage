package ru.netology.cloudstorage.security;

public interface TokenStore {
    void invalidateToken(String token);
    void storeToken(String username, String token);
    boolean containsToken(String username, String token);
}
