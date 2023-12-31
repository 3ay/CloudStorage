package ru.netology.cloudstorage.methods;

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
import org.springframework.test.util.ReflectionTestUtils;
import ru.netology.cloudstorage.dto.FileItemDTO;
import ru.netology.cloudstorage.exception.ErrorGetFiles;
import ru.netology.cloudstorage.exception.ErrorInputData;
import ru.netology.cloudstorage.service.impl.StoreServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GetAllFilesTest {
    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private StoreServiceImpl storeService;

    @BeforeEach
    public void setup() throws Exception {
        String bucketName = "store-bucket";
        ReflectionTestUtils.setField(storeService, "bucketName", bucketName);

        Item item1 = mock(Item.class);
        String testFilename = "test.txt";
        lenient().when(item1.objectName()).thenReturn(testFilename);
        lenient().when(item1.size()).thenReturn(1234L);

        Result<Item> result1 = mock(Result.class);
        lenient().when(result1.get()).thenReturn(item1);

        Iterable<Result<Item>> results = List.of(result1);
        lenient().when(minioClient.listObjects(any(ListObjectsArgs.class))).thenReturn(results);
    }


    @Test
    public void testListFiles() {
        List<FileItemDTO> files = new ArrayList<>();
        Iterable<Result<Item>> items = storeService.listFiles(1);
        for (Result<Item> itemResult : items) {
            try {
                Item item = itemResult.get();
                FileItemDTO fileItemDTO = new FileItemDTO(item.objectName(), item.size());
                files.add(fileItemDTO);
            } catch (Exception e) {
                throw new ErrorGetFiles("Error to get files");
            }
        }
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
