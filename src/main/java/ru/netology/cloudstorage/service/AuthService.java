package ru.netology.cloudstorage.service;

public interface AuthService {
     String login(String username, String password);
     void logout(String token);
}
