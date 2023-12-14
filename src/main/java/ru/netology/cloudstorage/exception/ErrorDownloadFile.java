package ru.netology.cloudstorage.exception;

public class ErrorDownloadFile extends RuntimeException{
    public ErrorDownloadFile(String message) {
        super(message);
    }
}
