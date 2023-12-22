package ru.netology.cloudstorage.methods;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.netology.cloudstorage.exception.ErrorDeleteFile;
import ru.netology.cloudstorage.exception.ErrorInputData;
import ru.netology.cloudstorage.service.StoreService;
import ru.netology.cloudstorage.service.impl.StoreServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteFileTest {
    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private StoreServiceImpl storeService;
    private final String bucketName = "store-bucket";
    private final String testFilename = "test.txt";

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(storeService, "bucketName", bucketName);
    }

    @Test
    void testDeleteFile() {
        Iterable<Result<Item>> iterable = mock(Iterable.class);
        Iterator<Result<Item>> iterator = mock(Iterator.class);
        when(iterator.hasNext()).thenReturn(true);
        when(iterable.iterator()).thenReturn(iterator);
        when(minioClient.listObjects(any(ListObjectsArgs.class))).thenReturn(iterable);
        try {
            storeService.deleteFile(testFilename);
            verify(minioClient).removeObject(
                    argThat(args -> args.bucket().equals(bucketName) && args.object().equals(testFilename))
            );
        } catch (Exception e) {
            throw new ErrorDeleteFile(e.getMessage());
        }
    }

    @Test
    void testDeleteFileWithEmptyFilename() {
        String testFilename = "";
        assertThrows(ErrorInputData.class, () -> storeService.deleteFile(testFilename));
    }
}
