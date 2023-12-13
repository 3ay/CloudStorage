package ru.netology.cloudstorage.controller;

import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.exception.ErrorInputData;
import ru.netology.cloudstorage.service.StoreService;
import ru.netology.cloudstorage.service.impl.StoreServiceImpl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
public class UploadFileController {
    private final StoreService storeService;
    private static final Logger log = LoggerFactory.getLogger(UploadFileController.class);
    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "filename") String fileName, @RequestParam("file") MultipartFile file) {
        log.info("uploadFile method called with filename: {}", fileName);
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
