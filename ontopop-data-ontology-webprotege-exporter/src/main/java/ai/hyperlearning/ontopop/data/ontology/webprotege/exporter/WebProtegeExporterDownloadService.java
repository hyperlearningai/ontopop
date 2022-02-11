package ai.hyperlearning.ontopop.data.ontology.webprotege.exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import reactor.core.publisher.Mono;

/**
 * WebProtege Exporter Download Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class WebProtegeExporterDownloadService {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebProtegeExporterDownloadService.class);
    
    @Autowired
    @Qualifier("webProtegeWebClient")
    private WebClient webClient;
    
    // WebProtege credentials
    private static final String WEBPROTEGE_USERNAME_ENV_KEY = "WEBPROTEGE_USERNAME";
    private static final String WEBPROTEGE_PASSWORD_ENV_KEY = "WEBPROTEGE_PASSWORD";
    
    // WebProtege Web Elements
    private static final String WEBPROTEGE_LOGIN_URL = "https://webprotege.stanford.edu/";
    private static final String WEBPROTEGE_DOWNLOAD_URI = "/download";
    private static final String WEBPROTEGE_LOGIN_FORM_CLASS_NAME = "wp-login__form";
    private static final String WEBPROTEGE_LOGIN_FORM_USERNAME_TEXTBOX_CLASS_NAME = "gwt-TextBox";
    private static final String WEBPROTEGE_LOGIN_FORM_PASSWORD_TEXTBOX_CLASS_NAME = "gwt-PasswordTextBox";
    private static final String WEBPROTEGE_LOGIN_FORM_SUBMIT_BUTTON_CLASS_NAME = "gwt-Button";
    private static final String WEBPROTEGE_PROJECT_LIST_TOPBAR_CLASS_NAME = "wp-topbar";
    
    // WebProtege export and download
    private static final String WEBPROTEGE_DOWNLOAD_PARAM_PROJECT = "project";
    private static final String WEBPROTEGE_DOWNLOAD_PARAM_REVISION = "revision";
    private static final String WEBPROTEGE_DOWNLOAD_PARAM_FORMAT = "format";
    private static final String WEBPROTEGE_DOWNLOAD_FORMAT = "owl";
    private static final String DOWNLOAD_TEMP_DIRECTORY_NAME_PREFIX = "webprotege";
    private String projectId;
    private int revision;
    private String downloadedZipAbsolutePath;
    private String extractedOwlAbsolutePath;
    
    // Selenium Web Driver
    private WebDriver webDriver;
    private static final boolean ENABLE_JAVASCRIPT = true;
    private static final boolean ENABLE_CSS = false;
    private static final boolean DOWNLOAD_IMAGES = false;
    private static final boolean ENABLE_DO_NOT_TRACK = true;
    private static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";
    private String jsessionIdCookieValue;
    
    public String run(String projectId, int revision) throws Exception {
        
        LOGGER.info("WebProtege exporter download service started.");
        this.projectId = projectId;
        this.revision = revision;
        
        try {
            
            // 1. Environment setup
            setup();
            
            // 2. Authenticate and get JSESSIONID cookie value
            authenticate();
            
            // 3. Download the ontology as an archived RDF/XML OWL file
            download();
            
            // 4. Unzip the downloaded archive and extract the OWL file
            unzip();
            
        } finally {
            
            // 5. Close all web clients
            cleanup();
            
        }
        
        LOGGER.info("WebProtege exporter download service finished.");
        return extractedOwlAbsolutePath;
        
    }
    
    /**
     * Initialise the HtmlUnit headless web driver
     */
    
    private void setup() throws NullPointerException {
        
        // Check that the WebProtege service account credentials have
        // been set as environment variables
        LOGGER.info("Checking that WebProtege service account credentials "
                + "have been set as environment variables.");
        if ( System.getenv(WEBPROTEGE_USERNAME_ENV_KEY) == null ) {
            LOGGER.error("{} has not been set as an environment variable", 
                    WEBPROTEGE_USERNAME_ENV_KEY);
            throw new NullPointerException(String.format("Please set %s "
                    + "as an environment variable.", WEBPROTEGE_USERNAME_ENV_KEY));
        }
        if ( System.getenv(WEBPROTEGE_PASSWORD_ENV_KEY) == null ) {
            LOGGER.error("{} has not been set as an environment variable", 
                    WEBPROTEGE_PASSWORD_ENV_KEY);
            throw new NullPointerException(String.format("Please set %s "
                    + "as an environment variable.", WEBPROTEGE_PASSWORD_ENV_KEY));
        }   
        
        // Initialize and configure the web driver
        LOGGER.info("Initializing the HtmlUnit headless web driver.");
        this.webDriver = new HtmlUnitDriver(ENABLE_JAVASCRIPT) {
            
            @Override
            protected com.gargoylesoftware.htmlunit.WebClient modifyWebClient(
                    com.gargoylesoftware.htmlunit.WebClient client) {
                final com.gargoylesoftware.htmlunit.WebClient htmlUnitWebClient = 
                        super.modifyWebClient(client);
                htmlUnitWebClient.getOptions().setCssEnabled(ENABLE_CSS);
                htmlUnitWebClient.getOptions().setDownloadImages(DOWNLOAD_IMAGES);
                htmlUnitWebClient.getOptions().setDoNotTrackEnabled(ENABLE_DO_NOT_TRACK);
                return htmlUnitWebClient;
            }
            
        };
        
    }
    
    /**
     * Authenticate and get JSESSIONID cookie value
     */
    
    private void authenticate() {
        
        // Load the WebProtege login page
        webDriver.get(WEBPROTEGE_LOGIN_URL);
        
        // Wait until the page had fully loaded by waiting until the
        // visibility of the login form
        FluentWait<WebDriver> fluentWait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class);
        fluentWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.className(WEBPROTEGE_LOGIN_FORM_CLASS_NAME)));
        
        // Get the username input box as a web element
        WebElement usernameInputBox = webDriver
                .findElement(By.className(WEBPROTEGE_LOGIN_FORM_CLASS_NAME))
                .findElement(By.className(WEBPROTEGE_LOGIN_FORM_USERNAME_TEXTBOX_CLASS_NAME));
        
        // Get the password input box as a web element
        WebElement passwordInputBox = webDriver
                .findElement(By.className(WEBPROTEGE_LOGIN_FORM_CLASS_NAME))
                .findElement(By.className(WEBPROTEGE_LOGIN_FORM_PASSWORD_TEXTBOX_CLASS_NAME));
        
        // Get the sign in button as a web element
        WebElement submitButton = webDriver
                .findElement(By.className(WEBPROTEGE_LOGIN_FORM_CLASS_NAME))
                .findElement(By.className(WEBPROTEGE_LOGIN_FORM_SUBMIT_BUTTON_CLASS_NAME));
        
        // Enter the WebProtege service account username
        usernameInputBox.sendKeys(System.getenv(WEBPROTEGE_USERNAME_ENV_KEY));

        // Enter the WebProtege service account password
        passwordInputBox.sendKeys(System.getenv(WEBPROTEGE_PASSWORD_ENV_KEY));
        
        // Click the submit button to login
        submitButton.click();
        
        // Wait until the login operation has completed
        // and the WebProtege project list is visible
        fluentWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.className(WEBPROTEGE_PROJECT_LIST_TOPBAR_CLASS_NAME)));
        
        // Wait until the JSESSIONID cookie has been set
        ExpectedCondition<Cookie> jsessionIdCookieEC = 
                new ExpectedCondition<Cookie>() {
            
            @Override
            public Cookie apply(WebDriver webDriver) {
                Cookie tokenCookie = webDriver.manage()
                        .getCookieNamed(JSESSIONID_COOKIE_NAME);
                if (tokenCookie != null) {
                    LOGGER.debug("Token Cookie added: {}", tokenCookie);
                    return tokenCookie;
                } else {
                    LOGGER.debug("Waiting for cookie...");
                    return null;
                }
            }
            
        };
        
        // Get the JSESSIONID cookie
        fluentWait.until(jsessionIdCookieEC);
        Cookie jsessionIdCookie = webDriver.manage()
                .getCookieNamed(JSESSIONID_COOKIE_NAME);
        jsessionIdCookieValue = jsessionIdCookie.getValue();
        
    }
    
    /**
     * Download the ontology as a RDF/XML OWL file
     * @throws IOException 
     */
    
    private void download() throws IOException {
        
        // Write the downloaded OWL file into a data buffer
        Mono<DataBuffer> dataBuffer = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(WEBPROTEGE_DOWNLOAD_URI)
                        .queryParam(WEBPROTEGE_DOWNLOAD_PARAM_PROJECT, projectId)
                        .queryParam(WEBPROTEGE_DOWNLOAD_PARAM_REVISION, revision)
                        .queryParam(WEBPROTEGE_DOWNLOAD_PARAM_FORMAT, WEBPROTEGE_DOWNLOAD_FORMAT)
                        .build())
                .cookie(JSESSIONID_COOKIE_NAME, jsessionIdCookieValue)
                .retrieve()
                .bodyToMono(DataBuffer.class);
        
        // Write the contents of the data buffer to the local filesystem
        String filename = projectId + "_" + revision + ".zip";
        Path downloadFilePath = Files.createTempFile("", filename);
        DataBufferUtils.write(
                dataBuffer, downloadFilePath, StandardOpenOption.CREATE)
            .share().block();
        downloadedZipAbsolutePath = downloadFilePath
                .toAbsolutePath().toString();
        
    }
    
    /**
     * Unzip the downloaded archive and extract the OWL file
     * @throws IOException 
     */
    
    private void unzip() throws IOException {
        
        // Extract the OWL file without the subfolder
        ZipFile zipFile = new ZipFile(downloadedZipAbsolutePath);
        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        for (FileHeader fileHeader : fileHeaders) {
            if ( fileHeader.getFileName()
                    .endsWith("." + WEBPROTEGE_DOWNLOAD_FORMAT) ) {
                Path tempDir = Files.createTempDirectory(
                        DOWNLOAD_TEMP_DIRECTORY_NAME_PREFIX);
                String owlFilename = projectId + "_" + revision + ".owl";
                zipFile.extractFile(fileHeader, 
                        tempDir.toAbsolutePath().toString(), 
                        owlFilename);
                extractedOwlAbsolutePath = tempDir.toAbsolutePath().toString()
                         + "/" + owlFilename;
                break;
            }
        }
        
        // Close the zip file
        zipFile.close();
        
    }
    
    /**
     * Close all web clients
     */
    
    private void cleanup() {
        
        // Dispose of the web driver
        if ( webDriver != null )
            webDriver.quit();
        
    }
    
}
