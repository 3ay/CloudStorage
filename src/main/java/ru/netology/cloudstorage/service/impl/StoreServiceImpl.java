package ru.netology.cloudstorage.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.exception.ErrorInputData;
import ru.netology.cloudstorage.service.StoreService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class StoreServiceImpl implements StoreService {

    @Value("${minio.bucket.name}")
    private String bucketName;
    @Autowired
    private MinioClient minioClient;
    private static final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);
    @Override
    public void uploadFile(String filename, MultipartFile file) {
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
}
