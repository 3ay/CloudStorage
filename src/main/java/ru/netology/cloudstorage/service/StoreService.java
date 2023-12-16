package ru.netology.cloudstorage.service;

import io.minio.errors.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileItemDTO;
import ru.netology.cloudstorage.exception.ErrorInputData;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface StoreService {
    void uploadFile(String filename, MultipartFile file);
    void deleteFile(String filename);
    InputStreamResource downloadFile(String filename);
    void editFileName(String oldFileName, String newFileName);
    List<FileItemDTO> listFiles(Integer limit);
}
