package ru.netology.cloudstorage.methods;

import io.minio.MinioClient;
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
import ru.netology.cloudstorage.service.impl.StoreServiceImpl;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteFileTest {
    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private StoreServiceImpl storeService;
    private final String bucketName = "store-bucket";
    private final String testFilename = "test.txt";

    @BeforeEach
    public void setup() throws Exception {
        ReflectionTestUtils.setField(storeService, "bucketName", bucketName);
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new ByteArrayInputStream("test content".getBytes())
        );
        storeService.uploadFile("test.txt", testFile);
    }

    @Test
    void testDeleteFile() throws ErrorDeleteFile {
        storeService.deleteFile(testFilename);
        try {
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
