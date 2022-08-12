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
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ai.hyperlearning.ontopop.exceptions.git.GitWebhookNotFoundException;
import ai.hyperlearning.ontopop.exceptions.graph.InvalidGremlinQueryException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyCreationAlreadyExistsException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyCreationException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataInvalidAuthorException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataInvalidFormatException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataParsingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPropertyGraphModellingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPutException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDeletionException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDiffInvalidRequestParametersException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDiffInvalidTimestampException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDiffProcessingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDownloadException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidTargetFormatException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidSourceOntologyDataException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidSourceFormatException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyNotFoundException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyUpdateSecretDataException;
import ai.hyperlearning.ontopop.exceptions.security.InvalidClientNameException;
import ai.hyperlearning.ontopop.exceptions.triplestore.InvalidSparqlQueryException;
import ai.hyperlearning.ontopop.exceptions.vendors.OntoKaiInvalidOntologyPayloadException;
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
        String localisedErrorMessage = getI18nErrorMessage(
                request, exception.getI18nKey());
        OntoPopExceptionResponseBody ontoPopExceptionResponseBody = 
                new OntoPopExceptionResponseBody(localisedErrorMessage);
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
     * @param i18nKey
     * @return
     */
    
    private String getI18nErrorMessage(WebRequest request, String i18nKey) {
        String lang = request.getParameter(LANG_REQUEST_PARAMETER_NAME);
        ResourceBundle bundle = StringUtils.hasText(lang) ? 
                ResourceBundle.getBundle(RESOURCE_BUNDLE_FILE_NAME_PREFIX, 
                        new Locale(lang)) : 
                    ResourceBundle.getBundle(RESOURCE_BUNDLE_FILE_NAME_PREFIX, 
                            Locale.ROOT);
        return bundle.getString(i18nKey);
    }
    
    /**
     * Handler - Bad Request (400)
     * @param exception
     * @param request
     * @return
     */
    
    @ExceptionHandler({
        InvalidSparqlQueryException.class, 
        InvalidGremlinQueryException.class, 
        OntologyDiffInvalidTimestampException.class, 
        OntologyDiffInvalidRequestParametersException.class, 
        OntologyDataInvalidFormatException.class, 
        OntologyDataInvalidAuthorException.class, 
        InvalidClientNameException.class, 
        OntoKaiInvalidOntologyPayloadException.class, 
        OntologyMapperInvalidSourceFormatException.class, 
        OntologyMapperInvalidTargetFormatException.class, 
        OntologyMapperInvalidSourceOntologyDataException.class, 
        WebProtegeInvalidProjectId.class })
    protected ResponseEntity<Object> handleBadRequest(
            OntoPopException exception, WebRequest request) {
        return handleException(exception, request, HttpStatus.BAD_REQUEST);
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
        GitWebhookNotFoundException.class, 
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
        OntologyCreationAlreadyExistsException.class })
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
        OntologyCreationException.class,
        OntologyUpdateSecretDataException.class, 
        OntologyDeletionException.class, 
        OntologyDownloadException.class, 
        OntologyDiffProcessingException.class, 
        OntologyDataPutException.class, 
        OntoKaiOntologyPayloadMappingException.class, 
        OntologyDataParsingException.class, 
        OntologyDataPropertyGraphModellingException.class, 
        WebProtegeMissingCredentials.class, 
        WebProtegeAuthenticationException.class })
    protected ResponseEntity<Object> handleInternalServerError(
            OntoPopException exception, WebRequest request) {
        return handleException(exception, request, 
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
