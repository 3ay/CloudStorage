package ru.netology.cloudstorage.service;

import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.exception.ErrorInputData;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface StoreService {
    void uploadFile(String filename, MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, ErrorInputData;
}
