package ru.netology.cloudstorage.service;

public interface AuthService {
     String login(String login, String password);
     void logout(String token);
}
