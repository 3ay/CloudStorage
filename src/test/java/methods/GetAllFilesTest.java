package methods;

import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.netology.cloudstorage.dto.FileItemDTO;
import ru.netology.cloudstorage.exception.ErrorInputData;
import ru.netology.cloudstorage.service.impl.StoreServiceImpl;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetAllFilesTest {
    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private StoreServiceImpl storeService;
    private final String bucketName = "store-bucket";
    private final String testFilename = "test.txt";
    @BeforeEach
    public void setup() throws Exception {
        ReflectionTestUtils.setField(storeService, "bucketName", bucketName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                testFilename,
                "text/plain",
                new ByteArrayInputStream("test content".getBytes())
        );
        storeService.uploadFile(testFilename, testFile);

        // Мокирование ответов для метода listObjects
        Item item1 = mock(Item.class);
        lenient().when(item1.objectName()).thenReturn(testFilename);
        lenient().when(item1.size()).thenReturn(1234L);

        Result<Item> result1 = mock(Result.class);
        lenient().when(result1.get()).thenReturn(item1);

        Iterable<Result<Item>> results = List.of(result1);
        lenient().when(minioClient.listObjects(any(ListObjectsArgs.class))).thenReturn(results);
    }
    @Test
    public void testListFiles(){
        List<FileItemDTO> files = storeService.listFiles(1);
        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("test.txt", files.get(0).getFilename());
        assertEquals(1234L, files.get(0).getSize());
    }

    @Test
    public void testListFilesWithInvalidLimit() {
        assertThrows(ErrorInputData.class, () -> storeService.listFiles(-1));
    }
}
