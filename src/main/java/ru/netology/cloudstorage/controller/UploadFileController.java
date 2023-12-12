package ru.netology.cloudstorage.controller;

import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.exception.ErrorInputData;
import ru.netology.cloudstorage.service.StoreService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
public class UploadFileController {
    private final StoreService storeService;

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "filename") String fileName, @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("before upload");
            storeService.uploadFile(fileName, file);
            System.out.println("after upload");
            return ResponseEntity.ok("Success upload");
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new ErrorInputData(e.getMessage());
        }
    }
}
