package ru.netology.cloudstorage.exception;

public class ErrorGetAllFiles extends RuntimeException{
    public ErrorGetAllFiles(String message) {
        super(message);
    }
}
