package ru.netology.cloudstorage.methods;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
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
import java.util.Iterator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EditFileNameTest {
    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private StoreServiceImpl storeService;

    private static final String BUCKET_NAME = "store-bucket";
    private static final String OLD_FILE_NAME = "test.txt";
    private static final String NEW_FILE_NAME = "newfile.txt";

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(storeService, "bucketName", BUCKET_NAME);
    }

    @Test
    void testEditFileName() throws Exception {
        Iterator<Result<Item>> iterator = mock(Iterator.class);
        when(iterator.hasNext()).thenReturn(true);
        Iterable<Result<Item>> iterable = mock(Iterable.class);
        when(iterable.iterator()).thenReturn(iterator);
        when(minioClient.listObjects(any(ListObjectsArgs.class))).thenReturn(iterable);

        storeService.editFileName(OLD_FILE_NAME, NEW_FILE_NAME);

        verify(minioClient).copyObject(argThat(args -> args.object().equals(NEW_FILE_NAME) && args.bucket().equals(BUCKET_NAME)));
        verify(minioClient).removeObject(argThat(args -> args.object().equals(OLD_FILE_NAME) && args.bucket().equals(BUCKET_NAME)));
    }

    @AfterEach
    public void cleanup() {
        storeService.deleteFile(NEW_FILE_NAME);
    }
}
