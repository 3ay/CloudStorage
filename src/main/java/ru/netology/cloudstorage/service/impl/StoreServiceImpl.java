package ru.netology.cloudstorage.service.impl;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileItemDTO;
import ru.netology.cloudstorage.exception.*;
import ru.netology.cloudstorage.service.StoreService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    @Value("${minio.bucket.name}")
    private String bucketName;
    @Autowired
    private MinioClient minioClient;
    private static final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);

    @Override
    public void uploadFile(String filename, MultipartFile file) {
        if (filename == null || filename.isEmpty() || filename.equals(" ")) {
            throw new ErrorInputData("Filename is null or empty");
        } else if (file.isEmpty()) {
            throw new ErrorInputData("File is empty");
        }
        log.info("start to upload");
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                System.out.println("create bucket");
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            System.out.println("Success upload");
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new ErrorInputData(e.getMessage());
        }

    }

    @Override
    public void deleteFile(String filename) {
        if (filename == null || filename.isEmpty() || filename.equals(" ")) {
            throw new ErrorInputData("Filename is null or empty");
        }
        try {
            log.info("Deleting file: {}", filename);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );
        } catch (Exception ex) {
            throw new ErrorDeleteFile(ex.getMessage());
        }
    }

    @Override
    public InputStreamResource downloadFile(String filename) {
        if (filename == null || filename.isEmpty() || filename.equals(" ")) {
            throw new ErrorInputData("Filename is null or empty");
        }
        try {
            return new InputStreamResource(minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            ));
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ErrorDownloadFile(e.getMessage());
        }
    }

    @Override
    public void editFileName(String oldFileName, String newFileName) {
        if (oldFileName == null || oldFileName.isEmpty() || oldFileName.equals(" ")
                || newFileName == null || newFileName.isEmpty() || newFileName.equals(" ")) {
            throw new ErrorInputData("oldFileName/newFileName is null or empty");
        }
        try {
            log.info("Renaming file from: {} to: {}", oldFileName, newFileName);
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(newFileName)
                            .source(CopySource.builder()
                                    .bucket(bucketName)
                                    .object(oldFileName)
                                    .build())
                            .build());
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(oldFileName)
                            .build());
        } catch (Exception e) {
            throw new ErrorEditFile("Failed to rename file: " + e.getMessage());
        }
    }

    public List<FileItemDTO> listFiles(Integer limit) {
        if (limit == null || limit < 0) {
            throw new ErrorInputData("limit is null or less than 0");
        }
        try {
            log.info("Listing files with limit: {}", limit);
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .maxKeys(limit)
                            .build());

            List<FileItemDTO> fileList = new ArrayList<>();
            for (Result<Item> result : results) {
                Item item = result.get();
                fileList.add(new FileItemDTO(item.objectName(), item.size()));
            }
            return fileList;
        } catch (Exception e) {
            throw new ErrorGetAllFiles("Failed to list files: " + e.getMessage());
        }
    }
}
