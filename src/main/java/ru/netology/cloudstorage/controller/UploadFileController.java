package ru.netology.cloudstorage.controller;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileItemDTO;
import ru.netology.cloudstorage.service.StoreService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
public class UploadFileController {
    private final StoreService storeService;
    private static final Logger log = LoggerFactory.getLogger(UploadFileController.class);

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "filename") String fileName, @RequestParam("file") MultipartFile file) {
        log.info("uploadFile method called with filename: {}", fileName);
        storeService.uploadFile(fileName, file);
        return ResponseEntity.ok("Success upload");
    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestParam(value = "filename") String fileName) {
        log.info("deleteFile method called with filename: {}", fileName);
        storeService.deleteFile(fileName);
        return ResponseEntity.ok("Success deleted");
    }

    @GetMapping("/file")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam(value = "filename") String fileName) {
        log.info("downloadFile method called with filename: {}", fileName);
        InputStreamResource resource = storeService.downloadFile(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @PutMapping("/file")
    public ResponseEntity<String> editFileName(@RequestParam(value = "filename") String fileName,
                                               @RequestBody String name) {
        log.info("editFileName method called with filename: {}", fileName);
        try {
            storeService.editFileName(fileName, name);
            return ResponseEntity.ok("File name successfully updated");
        } catch (Exception e) {
            log.error("Error updating file name: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error updating file name");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listFiles(@RequestParam(value = "limit", required = false) Integer limit) {
        log.info("listFiles method called with limit: {}", limit);
        Iterable<Result<Item>> results = storeService.listFiles(limit);
        List<FileItemDTO> fileList = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                Item item = result.get();
                fileList.add(new FileItemDTO(item.objectName(), item.size()));
            }
        } catch (Exception e) {
            log.error("Error get files: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error get files");
        }
        return ResponseEntity.ok(fileList);
    }
}
