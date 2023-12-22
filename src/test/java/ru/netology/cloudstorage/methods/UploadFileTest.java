package ru.netology.cloudstorage.methods;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.BucketExistsArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.netology.cloudstorage.exception.ErrorInputData;
import ru.netology.cloudstorage.service.impl.StoreServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UploadFileTest {
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
    public void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                testFilename,
                "text/plain",
                "test content".getBytes()
        );

        storeService.uploadFile(testFilename, file);
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void testUploadFileWithEmptyFilename() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "",
                "text/plain",
                "test content".getBytes()
        );

        assertThrows(ErrorInputData.class, () -> storeService.uploadFile("", file));
    }

    @Test
    public void testUploadFileWithEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                testFilename,
                "text/plain",
                new byte[0]
        );
        assertThrows(ErrorInputData.class, () -> storeService.uploadFile(testFilename, file));
    }
}
