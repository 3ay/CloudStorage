package ru.netology.cloudstorage.exception;

public class ErrorDeleteFile extends RuntimeException {
    public ErrorDeleteFile(String message) {
        super(message);
    }
}
