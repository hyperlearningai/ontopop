package ai.hyperlearning.ontopop.webprotege;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeAuthenticationException;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeInvalidProjectId;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeMissingCredentials;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeProjectAccessException;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import reactor.core.publisher.Mono;

/**
 * WebProtege Downloader Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class WebProtegeDownloader {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebProtegeDownloader.class);
    
    @Autowired
    @Qualifier("webProtegeWebClient")
    private WebClient webClient;
    
    // WebProtege credentials
    private static final String WEBPROTEGE_USERNAME_ENV_KEY = 
            "WEBPROTEGE_USERNAME";
    private static final String WEBPROTEGE_PASSWORD_ENV_KEY = 
            "WEBPROTEGE_PASSWORD";
    private static final String WEBPROTEGE_WEBDRIVER_TIMEOUT_ENV_KEY = 
            "WEBPROTEGE_WEBDRIVER_TIMEOUT";
    
    // WebProtege project ID validation rules
    private static final int WEBPROTEGE_PROJECT_ID_LENGTH = 36;
    
    // WebProtege web elements
    private static final String WEBPROTEGE_LOGIN_URL = "https://webprotege.stanford.edu/";
    private static final String WEBPROTEGE_DOWNLOAD_URI = "/download";
    private static final String WEBPROTEGE_LOGIN_FORM_CLASS_NAME = "wp-login__form";
    private static final String WEBPROTEGE_LOGIN_FORM_USERNAME_TEXTBOX_CLASS_NAME = "gwt-TextBox";
    private static final String WEBPROTEGE_LOGIN_FORM_PASSWORD_TEXTBOX_CLASS_NAME = "gwt-PasswordTextBox";
    private static final String WEBPROTEGE_LOGIN_FORM_SUBMIT_BUTTON_CLASS_NAME = "gwt-Button";
    private static final String WEBPROTEGE_PROJECT_LIST_TOPBAR_CLASS_NAME = "wp-topbar";
    
    // WebProtege download query parameters
    private static final String WEBPROTEGE_DOWNLOAD_PARAM_PROJECT = "project";
    private static final String WEBPROTEGE_DOWNLOAD_PARAM_REVISION = "revision";
    private static final String WEBPROTEGE_DOWNLOAD_PARAM_FORMAT = "format";
    private static final String WEBPROTEGE_DOWNLOAD_FORMAT = "owl";
    private static final String DOWNLOAD_TEMP_DIRECTORY_NAME_PREFIX = "webprotege";
    private String projectId = null;
    private Integer revision = null;
    
    // WebProtege download local persistence
    private String downloadedZipAbsolutePath = null;
    private String extractedOwlAbsolutePath = null;
    private boolean downloaded = false;
    
    // Selenium Web Driver
    private WebDriver webDriver = null;
    private static final boolean ENABLE_JAVASCRIPT = true;
    private static final boolean ENABLE_CSS = false;
    private static final boolean DOWNLOAD_IMAGES = false;
    private static final boolean ENABLE_DO_NOT_TRACK = true;
    private static final int TIMEOUT_DEFAULT = 30;
    private Integer webDriverTimeout = null;
    
    /**
     * Run the end-to-end WebProtege downloader service
     * @param projectId
     * @param revision
     * @param webDriverTimeout
     * @return
     * @throws IOException
     * @throws WebProtegeMissingCredentials
     * @throws WebProtegeInvalidProjectId
     * @throws WebProtegeAuthenticationException
     * @throws WebProtegeProjectAccessException
     */
    
    public String run(String projectId, Integer revision, 
            Integer webDriverTimeout) 
            throws IOException, 
            WebProtegeMissingCredentials, 
            WebProtegeInvalidProjectId, 
            WebProtegeAuthenticationException, 
            WebProtegeProjectAccessException {
        
        // Set the WebProtege project ID and revision number
        LOGGER.info("WebProtege download service started.");
        this.projectId = projectId;
        this.revision = revision;
        this.downloadedZipAbsolutePath = null;
        this.extractedOwlAbsolutePath = null;
        this.downloaded = false;
        this.webDriver = null;
        this.webDriverTimeout = webDriverTimeout;
        
        // Run the download service
        try {
            
            // 1. WebProtege credentials and project ID validation
            validate();
            
            // 2. Attempt immediate download
            downloadIfAuthSessionExists();
            if ( !downloaded ) {
                
                // 3. WebDriver setup
                setup();
                
                // 4. WebProtege authentication
                authenticate();
                
                // 5. RDF/XML OWL archive export and download
                download();
                
            }
            
            // 6. OWL archive unzip
            unzip();
            
        } finally {
            
            // 7. Close the WebDriver
            cleanup();
        
        }
        
        LOGGER.info("WebProtege download service finished.");
        return extractedOwlAbsolutePath;
        
    }
    
    /**
     * Validate the WebProtege credentials and project ID
     */
    
    private void validate() 
            throws WebProtegeMissingCredentials, 
            WebProtegeInvalidProjectId {
        
        // Validation rules
        LOGGER.info("Validating the WebProtege credentials and "
                + "given project ID.");
        
        // Validate the WebProtege credentials - username
        if ( System.getenv(WEBPROTEGE_USERNAME_ENV_KEY) == null )
            throw new WebProtegeMissingCredentials();
        
        // Validate the WebProtege credentials - password
        if ( System.getenv(WEBPROTEGE_PASSWORD_ENV_KEY) == null )
            throw new WebProtegeMissingCredentials();
        
        // Validate the WebProtege project ID - emptiness
        if ( StringUtils.isAllBlank(projectId) )
            throw new WebProtegeInvalidProjectId();
        
        // Validate the WebProtege project ID - characters
        if ( !StringUtils.isAlphanumeric(projectId.replace("-", "")) )
            throw new WebProtegeInvalidProjectId();
        
        // Validate the WebProtege project ID - length
        if ( projectId.length() != WEBPROTEGE_PROJECT_ID_LENGTH )
            throw new WebProtegeInvalidProjectId();
        
    }
    
    /**
     * Attempt an immediate download if a JSESSION cookie already exists
     * @throws IOException
     */
    
    private void downloadIfAuthSessionExists() throws IOException {
        
        // Attempt to download immediately using an existing 
        // JSESSIONID cookie
        LOGGER.info("Attempting to immediately download the given WebProtege "
                + "project using the existing JSESSIONID cookie.");
        if (WebProtegeAuthSession.getJSessionIdCookieValue() != null) {
            try {
                download();
                LOGGER.info("Successfully downloaded the given WebProtege "
                        + "project immediately.");
            } catch (WebProtegeProjectAccessException e) {
                LOGGER.warn("An error was encountered when attempting an "
                        + "immediate download using the existing "
                        + "JSESSIONID cookie.");
                LOGGER.info("Proceeding to authenticate using a headless "
                        + "WebDriver instead.");
            }
        }
        
    }
    
    /**
     * Setup the WebDriver
     */
    
    private void setup() {
        
        // Set the WebDriver timeout duration
        LOGGER.debug("Initialising a headless WebDriver.");
        if ( webDriverTimeout == null ) {
            webDriverTimeout = TIMEOUT_DEFAULT;
            if (System.getenv(WEBPROTEGE_WEBDRIVER_TIMEOUT_ENV_KEY) != null) {
                try {
                    webDriverTimeout = Integer.valueOf(System.getenv(
                            WEBPROTEGE_WEBDRIVER_TIMEOUT_ENV_KEY));
                } catch (Exception e) {
                    
                }
            }
        }
        
        // Instantiate and configure a new web driver instance
        webDriver = new HtmlUnitDriver(ENABLE_JAVASCRIPT) {
            
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
        
        // Logging
        LOGGER.debug("Initialised a new headless WebDriver.");
        LOGGER.debug("Set the WebDriver timeout duration to {} seconds", 
                webDriverTimeout);
        
    }
    
    /**
     * Authenticate and get JSESSIONID cookie value
     */
    
    private void authenticate()
            throws WebProtegeAuthenticationException {
        
        // Load the WebProtege login page
        LOGGER.debug("Authenticating with WebProtege using a "
                + "headless WebDriver.");
        webDriver.get(WEBPROTEGE_LOGIN_URL);
        
        // Initialize a wait condition implementation
        FluentWait<WebDriver> fluentWait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(webDriverTimeout))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class);
        
        // Test to see if an authenticated session already exists.
        // This can be tested by the presence of the WebProtege project list
        // which only appears if successfully authenticated
        try {
            
            // Wait for the WebProtege project list to become visible
            LOGGER.info("Testing whether an authenticated session already exists.");
            fluentWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.className(WEBPROTEGE_PROJECT_LIST_TOPBAR_CLASS_NAME)));
            LOGGER.info("Authenticated session already exists. Proceeding to "
                    + "obtain the JSESSIONID.");
            
            // Wait until the JSESSIONID cookie has been set
            ExpectedCondition<Cookie> jsessionIdCookieEC = 
                    new ExpectedCondition<Cookie>() {
                
                @Override
                public Cookie apply(WebDriver webDriver) {
                    Cookie tokenCookie = webDriver.manage()
                            .getCookieNamed(WebProtegeAuthSession
                                    .JSESSIONID_COOKIE_NAME);
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
                    .getCookieNamed(WebProtegeAuthSession
                            .JSESSIONID_COOKIE_NAME);
            WebProtegeAuthSession.setJSessionIdCookieValue(
                    jsessionIdCookie.getValue());
            LOGGER.info("JSESSIONID successfully obtained: {}", 
                    WebProtegeAuthSession.getJSessionIdCookieValue());
            
        } catch (TimeoutException e) {
            
            // Wait until the page had fully loaded by waiting until the
            // visibility of the login form
            LOGGER.info("An authenticated session does NOT already exist. "
                    + "Proceeding to authenticate.");
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
                            .getCookieNamed(WebProtegeAuthSession
                                    .JSESSIONID_COOKIE_NAME);
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
                    .getCookieNamed(WebProtegeAuthSession
                            .JSESSIONID_COOKIE_NAME);
            WebProtegeAuthSession.setJSessionIdCookieValue(
                    jsessionIdCookie.getValue());
            LOGGER.info("JSESSIONID successfully obtained: {}", 
                    WebProtegeAuthSession.getJSessionIdCookieValue());
            
        }
        
        // Validate that the JSESSIONID has been obtained
        if ( StringUtils.isAllBlank(WebProtegeAuthSession
                .getJSessionIdCookieValue()) )
            throw new WebProtegeAuthenticationException();
        
    }
    
    /**
     * Download the ontology as a RDF/XML OWL file
     * to the local file system
     * @throws IOException 
     */
    
    private void download() throws IOException, 
            WebProtegeProjectAccessException {
        
        // Write the downloaded OWL file into a data buffer
        LOGGER.debug("Downloading WebProtege project ID {}.", projectId);
        Mono<DataBuffer> dataBuffer = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(WEBPROTEGE_DOWNLOAD_URI)
                        .queryParam(WEBPROTEGE_DOWNLOAD_PARAM_PROJECT, projectId)
                        .queryParamIfPresent(WEBPROTEGE_DOWNLOAD_PARAM_REVISION, Optional.ofNullable(revision))
                        .queryParam(WEBPROTEGE_DOWNLOAD_PARAM_FORMAT, WEBPROTEGE_DOWNLOAD_FORMAT)
                        .build())
                .cookie(WebProtegeAuthSession.JSESSIONID_COOKIE_NAME, 
                        WebProtegeAuthSession.getJSessionIdCookieValue())
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.UNAUTHORIZED),
                    error -> Mono.error(new WebProtegeProjectAccessException()))
                .bodyToMono(DataBuffer.class);
        
        // Write the contents of the data buffer to the local filesystem
        String filename = revision != null ? 
                projectId + "_" + revision + ".zip" : 
                    projectId + "_" + "LATEST" + ".zip";
        Path downloadFilePath = Files.createTempFile("", filename);
        DataBufferUtils.write(dataBuffer, 
                downloadFilePath, StandardOpenOption.CREATE)
            .share()
            .doOnError(error -> {
                if ( error.getMessage().contains(
                        String.valueOf(HttpStatus.UNAUTHORIZED.value())) ||
                        error.getMessage().contains(
                                String.valueOf(HttpStatus.FORBIDDEN.value())) || 
                        error.getMessage().contains(
                                String.valueOf(HttpStatus.NOT_FOUND.value())) )
                        throw new WebProtegeProjectAccessException();
                })
            .block();
        downloadedZipAbsolutePath = downloadFilePath
                .toAbsolutePath().toString();
        downloaded = true;
        LOGGER.info("Downloaded WebProtege project ID {} (revision number {}) "
                + "to: {}", projectId, revision, downloadedZipAbsolutePath);
        
    }
    
    /**
     * Unzip the downloaded archive and extract the OWL file
     * @throws IOException 
     */
    
    private void unzip() throws IOException {
        
        // Extract the OWL file without the subfolder
        if (downloaded) {
            LOGGER.debug("Extracting WebProtege project ID {}.", projectId);
            try (ZipFile zipFile = new ZipFile(downloadedZipAbsolutePath)) {
                List<FileHeader> fileHeaders = zipFile.getFileHeaders();
                for (FileHeader fileHeader : fileHeaders) {
                    if ( fileHeader.getFileName()
                            .endsWith("." + WEBPROTEGE_DOWNLOAD_FORMAT) ) {
                        Path tempDir = Files.createTempDirectory(
                                DOWNLOAD_TEMP_DIRECTORY_NAME_PREFIX);
                        String owlFilename = revision != null ? 
                                projectId + "_" + revision + ".owl" : 
                                    projectId + "_" + "LATEST" + ".owl";
                        zipFile.extractFile(fileHeader, 
                                tempDir.toAbsolutePath().toString(), 
                                owlFilename);
                        extractedOwlAbsolutePath = tempDir.toAbsolutePath()
                                .toString() + File.separator + owlFilename;
                        LOGGER.info("Extracted WebProtege project ID {} "
                                + "(revision number {}) to: {}", projectId, 
                                revision, extractedOwlAbsolutePath);
                        break;
                    }
                }
            }
        }
        
    }
    
    /**
     * Close all web clients
     */
    
    private void cleanup() {
        
        // Close the current window
        if ( webDriver != null ) {
            try {
                LOGGER.debug("Closing the headless WebDriver.");
                webDriver.quit();
            } catch (Exception e) {
                
            }
        }
        
    }

}
