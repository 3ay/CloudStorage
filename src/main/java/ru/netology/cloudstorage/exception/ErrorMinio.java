package ru.netology.cloudstorage.exception;

public class ErrorMinio extends RuntimeException{
    public ErrorMinio(String message) {
        super(message);
    }
}
