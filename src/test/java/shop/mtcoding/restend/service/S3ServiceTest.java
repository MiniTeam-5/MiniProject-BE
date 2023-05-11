package shop.mtcoding.restend.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
public class S3ServiceTest {

    @Autowired
    private S3Service s3Service;

    @Test
    public void upload_test() throws IOException {
        // given
        MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test".getBytes());

        // when
        String url = s3Service.upload(file);

        // then
        Assertions.assertThat(url).isNotBlank();
        Assertions.assertThat(url).startsWith("https://lupinbucket.s3.ap-northeast-2.amazonaws.com");
    }
}

