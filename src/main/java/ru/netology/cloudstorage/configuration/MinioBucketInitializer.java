package ru.netology.cloudstorage.configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.netology.cloudstorage.exception.ErrorMinio;
import ru.netology.cloudstorage.service.impl.StoreServiceImpl;

import javax.annotation.PostConstruct;

@Component
public class MinioBucketInitializer {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;
    private static final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);

    @PostConstruct
    public void init() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Created bucket: " + bucketName);
            }
        } catch (Exception e) {
            throw new ErrorMinio(e.getMessage());
        }
    }
}
