package ru.netology.cloudstorage.dto;

import lombok.Data;

@Data
public class ExceptionDTO {
    private String message;
    private Integer id;

    public ExceptionDTO(String message, Integer id) {
        this.message = message;
        this.id = id;
    }
}
