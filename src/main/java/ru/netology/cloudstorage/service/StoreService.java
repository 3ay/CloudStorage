package ru.netology.cloudstorage.service;

import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface StoreService {
    void uploadFile(String filename, MultipartFile file);
    void deleteFile(String filename);
    InputStreamResource downloadFile(String filename);
    void editFileName(String oldFileName, String newFileName);
    Iterable<Result<Item>> listFiles(Integer limit);
}
