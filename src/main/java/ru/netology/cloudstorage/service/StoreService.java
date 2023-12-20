package ru.netology.cloudstorage.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileItemDTO;

import java.util.List;

public interface StoreService {
    void uploadFile(String filename, MultipartFile file);
    void deleteFile(String filename);
    InputStreamResource downloadFile(String filename);
    void editFileName(String oldFileName, String newFileName);
    List<FileItemDTO> listFiles(Integer limit);
}
