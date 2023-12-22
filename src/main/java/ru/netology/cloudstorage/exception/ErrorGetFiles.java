package ru.netology.cloudstorage.exception;

public class ErrorGetFiles extends RuntimeException{
    public ErrorGetFiles(String message) {
        super(message);
    }
}
