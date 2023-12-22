package ru.netology.cloudstorage.security;

public interface TokenStore {
    void invalidateToken(String token);
    void storeToken(String login, String token);
    boolean containsToken(String login, String token);
}
