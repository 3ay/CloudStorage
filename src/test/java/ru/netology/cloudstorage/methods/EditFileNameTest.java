package ru.netology.cloudstorage.methods;

import io.minio.MinioClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.netology.cloudstorage.service.impl.StoreServiceImpl;

import java.io.ByteArrayInputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EditFileNameTest {
    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private StoreServiceImpl storeService;

    @BeforeEach
    public void setup() throws Exception {
        ReflectionTestUtils.setField(storeService, "bucketName", "store-bucket");
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new ByteArrayInputStream("test content".getBytes())
        );
        storeService.uploadFile("test.txt", testFile);
    }

    @Test
    void testEditFileName() throws Exception {
        String oldFileName = "test.txt";
        String newFileName = "newfile.txt";

        storeService.editFileName(oldFileName, newFileName);

        verify(minioClient).copyObject(any());
        verify(minioClient).removeObject(any());
    }
    @AfterEach
    public void cleanup() throws Exception {
        storeService.deleteFile("newfile.txt");
    }

}
