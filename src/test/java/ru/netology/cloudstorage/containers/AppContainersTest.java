package ru.netology.cloudstorage.containers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.netology.cloudstorage.exception.ErrorDownloadFile;
import ru.netology.cloudstorage.exception.ErrorInputData;
import ru.netology.cloudstorage.service.AuthService;
import ru.netology.cloudstorage.service.StoreService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AppContainersTest {
    private final static String ENDPOINT_FILE = "/cloud/file";
    private final static String ENDPOINT_LIST = "/cloud/list";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthService authService;
    @MockBean
    private StoreService storeService;
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("postgres")
            .withPassword("password")
            .withDatabaseName("postgres");
    @Container
    public static GenericContainer<?> minio = new GenericContainer<>(DockerImageName.parse("minio/minio"))
            .withExposedPorts(9000)
            .withEnv("MINIO_ROOT_USER", "miniokey")
            .withEnv("MINIO_ROOT_PASSWORD", "minio123secret")
            .withCommand("server /data");
    @Container
    private static final GenericContainer<?> appContainer = new GenericContainer<>("cloud-storage_app:latest")
            .withExposedPorts(7070);

    @DynamicPropertySource
    public static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> String.format("jdbc:postgresql://localhost:%s/postgres", postgres.getMappedPort(5432)));
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        String minioUrl = String.format("http://%s:%d", minio.getHost(), minio.getMappedPort(9000));
        registry.add("minio.url", () -> minioUrl);
        registry.add("minio.root-user", () -> "miniokey");
        registry.add("minio.root-password", () -> "minio123secret");
        registry.add("minio.bucket.name", () -> "store-bucket");
    }

    @LocalServerPort
    private int port;

    private String getAuthToken() {
        String authToken =  "Bearer ".concat(authService.login("user1", "password1"));
        return authToken;
    }

    @Test
    void testUploadFileError() throws Exception {
        String filename = " ";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getAuthToken());
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.txt", "text/plain", new byte[0]);
        doThrow(new ErrorInputData("Filename is null or empty")).when(storeService).uploadFile(filename, mockMultipartFile);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/cloud/file")
                        .file(mockMultipartFile)
                        .param("filename", filename)
                        .headers(headers))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(containsString("Error input data:  Filename is null or empty")));
    }

    @Test
    void testDeleteFileError() throws Exception {
        String filename = "nonexistentfile.txt";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getAuthToken());
        mockMvc.perform(MockMvcRequestBuilders.delete(ENDPOINT_FILE + "?filename=" + filename)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(containsString("Error delete file")));
    }
    @Test
    public void testDownloadFileError() throws Exception {
        String filename = "nonexistent-file.txt";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getAuthToken());
        doThrow(new ErrorDownloadFile("File not found")).when(storeService).downloadFile(filename);

        mockMvc.perform(MockMvcRequestBuilders.get("/cloud/file")
                        .param("filename", filename)
                        .headers(headers))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(containsString("Error download file:  File not found")));
    }
}
