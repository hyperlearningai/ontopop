package ai.hyperlearning.ontopop.exceptions;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ai.hyperlearning.ontopop.exceptions.ontology.OntologyCreateConflictException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyCreateException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataInvalidException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataParsingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPipelineException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDeleteException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDiffInvalidRequestException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDiffException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDownloadException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidRequestException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyNotFoundException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyUpdateException;
import ai.hyperlearning.ontopop.exceptions.security.InvalidClientNameException;
import ai.hyperlearning.ontopop.exceptions.vendors.OntoKaiOntologyInvalidPayloadException;
import ai.hyperlearning.ontopop.exceptions.vendors.OntoKaiOntologyPayloadMappingException;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeAuthenticationException;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeInvalidProjectId;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeMissingCredentials;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeProjectAccessException;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeWebhookNotFoundException;

/**
 * OntoPop Exception Handler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ControllerAdvice
public class OntoPopExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String LANG_REQUEST_PARAMETER_NAME = "lang";
    private static final String RESOURCE_BUNDLE_FILE_NAME_PREFIX = 
            "ErrorMessages";
    private static final ObjectWriter WRITER = 
            new ObjectMapper().writer().withDefaultPrettyPrinter();
    
    /**
     * Exception Handler
     * @param exception
     * @param request
     * @param httpStatus
     * @return
     * @throws JsonProcessingException 
     */
    
    private ResponseEntity<Object> handleException(OntoPopException exception, 
            WebRequest request, HttpStatus httpStatus) {
        
        // Construct the response body
        String localisedErrorMessage = getErrorMessage(
                request, exception.getErrorKey());
        OntoPopExceptionResponseBody ontoPopExceptionResponseBody = 
                new OntoPopExceptionResponseBody(exception.getErrorKey(), 
                        localisedErrorMessage);
        HttpHeaders headers = new HttpHeaders();
        String responseBody = null;
        try {
            responseBody = WRITER.writeValueAsString(
                    ontoPopExceptionResponseBody);
            headers.setContentType(MediaType.APPLICATION_JSON);
        } catch (JsonProcessingException e) {
            responseBody = localisedErrorMessage;
            headers.setContentType(MediaType.TEXT_PLAIN);
        }
        
        // Return a response
        return handleExceptionInternal(exception, responseBody, 
                headers, httpStatus, request);
        
    }
    
    /**
     * Get the localised error message for this exception
     * @param request
     * @param errorKey
     * @return
     */
    
    private String getErrorMessage(WebRequest request, String errorKey) {
        String lang = request.getParameter(LANG_REQUEST_PARAMETER_NAME);
        ResourceBundle bundle = StringUtils.hasText(lang) ? 
                ResourceBundle.getBundle(RESOURCE_BUNDLE_FILE_NAME_PREFIX, 
                        new Locale(lang)) : 
                    ResourceBundle.getBundle(RESOURCE_BUNDLE_FILE_NAME_PREFIX, 
                            Locale.ROOT);
        return bundle.getString(errorKey);
    }
    
    /**
     * Handler - Bad Request (400)
     * @param exception
     * @param request
     * @return
     */
    
    @ExceptionHandler({
        InvalidClientNameException.class, 
        OntoKaiOntologyInvalidPayloadException.class, 
        OntologyDataInvalidException.class, 
        OntologyDiffInvalidRequestException.class, 
        OntologyMapperInvalidRequestException.class, 
        WebProtegeInvalidProjectId.class })
    protected ResponseEntity<Object> handleBadRequest(
            OntoPopException exception, WebRequest request) {
        return handleException(exception, request, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({
        MaxUploadSizeExceededException.class })
    protected ResponseEntity<Object> handleMaxUploadSizeExceeded(
        MaxUploadSizeExceededException exception, WebRequest request) {
        OntoPopException ontoPopMaxUploadSizeExceededException = 
                new OntologyMapperInvalidRequestException(
                        OntologyMapperInvalidRequestException
                            .ErrorKey.INVALID_ONTOLOGY_DATA_FILE_SIZE);
        return handleException(ontoPopMaxUploadSizeExceededException, 
                request, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handler - Forbidden (403)
     * @param exception
     * @param request
     * @return
     */
    
    @ExceptionHandler({
        WebProtegeProjectAccessException.class })
    protected ResponseEntity<Object> handleForbidden(
            OntoPopException exception, WebRequest request) {
        return handleException(exception, request, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handler - Not Found (404)
     * @param exception
     * @param request
     * @return
     */
    
    @ExceptionHandler({
        OntologyNotFoundException.class,
        WebProtegeWebhookNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(
            OntoPopException exception, WebRequest request) {
        return handleException(exception, request, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handler - Conflict (409)
     * @param exception
     * @param request
     * @return
     */
    
    @ExceptionHandler({
        OntologyCreateConflictException.class })
    protected ResponseEntity<Object> handleConflict(
            OntoPopException exception, WebRequest request) {
        return handleException(exception, request, HttpStatus.CONFLICT);
    }

    /**
     * Handler - Internal Server Error (500)
     * @param exception
     * @param request
     * @return
     */
    
    @ExceptionHandler({
        OntoKaiOntologyPayloadMappingException.class, 
        OntologyCreateException.class,
        OntologyDataParsingException.class, 
        OntologyDataPipelineException.class, 
        OntologyDeleteException.class, 
        OntologyDiffException.class, 
        OntologyDownloadException.class, 
        OntologyUpdateException.class, 
        WebProtegeAuthenticationException.class, 
        WebProtegeMissingCredentials.class })
    protected ResponseEntity<Object> handleInternalServerError(
            OntoPopException exception, WebRequest request) {
        return handleException(exception, request, 
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
