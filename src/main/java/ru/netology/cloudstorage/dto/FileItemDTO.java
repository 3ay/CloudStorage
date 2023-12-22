package ru.netology.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@Getter
@Validated
public class FileItemDTO {
    private String filename;
    private long size;
}
