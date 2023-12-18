package methods;

import io.minio.*;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.netology.cloudstorage.exception.ErrorDownloadFile;
import ru.netology.cloudstorage.exception.ErrorInputData;
import ru.netology.cloudstorage.service.impl.StoreServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class DownloadFileTest {
    @InjectMocks
    private StoreServiceImpl storeService;
    private final String bucketName = "store-bucket";
    private final String testFilename = "test.txt";
    @Mock
    private MinioClient minioClient;

    @BeforeEach
    public void setup() throws Exception {
        ReflectionTestUtils.setField(storeService, "bucketName", bucketName);
        InputStream fakeInputStream = new ByteArrayInputStream("test content".getBytes());

        GetObjectResponse getObjectResponse = mock(GetObjectResponse.class, withSettings()
                .useConstructor(null, null, null, null, fakeInputStream)
                .defaultAnswer(CALLS_REAL_METHODS));

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);

        ReflectionTestUtils.setField(storeService, "minioClient", minioClient);
    }


    @Test
    void testDownloadFile() throws IOException {
        InputStreamResource result = storeService.downloadFile(testFilename);
        assertNotNull(result);
        assertEquals("test content", new String(result.getInputStream().readAllBytes()));
    }
    @Test
    void testDownloadFileWithEmptyFilename() {
        String testFilename = "";

        assertThrows(ErrorInputData.class, () -> storeService.downloadFile(testFilename));
    }
}
