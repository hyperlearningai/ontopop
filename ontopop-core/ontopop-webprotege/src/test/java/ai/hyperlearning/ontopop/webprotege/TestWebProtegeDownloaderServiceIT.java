package ai.hyperlearning.ontopop.webprotege;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeAuthenticationException;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeInvalidProjectId;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeMissingCredentials;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeProjectAccessException;

/**
 * Integration Tests - WebProtege Downloader Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestWebProtegeDownloaderAppIT.class)
@ActiveProfiles("integration-test-webprotege-downloader")
@TestMethodOrder(OrderAnnotation.class)
class TestWebProtegeDownloaderServiceIT {
    
    // WebProtege credentials
    private static final String WEBPROTEGE_USERNAME_ENV_KEY = 
            "WEBPROTEGE_USERNAME";
    private static final String WEBPROTEGE_PASSWORD_ENV_KEY = 
            "WEBPROTEGE_PASSWORD";
    
    // Test parameters
    private static final String WEBPROTEGE_PROJECT_ID_VALID = 
            "c9589912-e17b-4156-a1ab-fa5b10862f54";
    
    @Autowired
    WebProtegeDownloader webProtegeDownloader;
    
    @Test
    @Order(1)
    void whenMissingCredentials_thenReturnsWebProtegeMissingCredentials() {
        if ( System.getenv(WEBPROTEGE_USERNAME_ENV_KEY) == null || 
                System.getenv(WEBPROTEGE_PASSWORD_ENV_KEY) == null) {
            assertThrows(WebProtegeMissingCredentials.class, () -> {
                webProtegeDownloader.run("abc-123", null, null);
            });
        }
    }
    
    @Test
    @Order(2)
    void whenValid_thenReturnsDownloadedFilePath() 
            throws WebProtegeMissingCredentials, 
            WebProtegeInvalidProjectId, 
            WebProtegeAuthenticationException, 
            WebProtegeProjectAccessException, 
            IOException {
        if ( System.getenv(WEBPROTEGE_USERNAME_ENV_KEY) != null && 
                System.getenv(WEBPROTEGE_PASSWORD_ENV_KEY) != null) {
            String downloadedOwlFilePath = webProtegeDownloader.run(
                    WEBPROTEGE_PROJECT_ID_VALID, null, null);
            Path path = Paths.get(downloadedOwlFilePath);
            String contents = Files.readString(path, StandardCharsets.UTF_8);
            assertTrue(Files.exists(path));
            assertFalse(StringUtils.isBlank(contents));
        }
    }

}
