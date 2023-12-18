package ru.netology.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileItemDTO {
    private String filename;
    private long size;
}
