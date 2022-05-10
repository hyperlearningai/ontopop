package ai.hyperlearning.ontopop.exceptions;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
 * Custom Global Exception Handler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ControllerAdvice
public class CustomGlobalExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler({OntologyNotFoundException.class,
            GitWebhookNotFoundException.class, 
            WebProtegeWebhookNotFoundException.class})
    public void springHandleNotFound(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({OntologyCreationException.class,
            OntologyUpdateSecretDataException.class, 
            OntologyDeletionException.class, 
            OntologyDownloadException.class, 
            OntologyDiffProcessingException.class, 
            OntologyDataPutException.class, 
            OntoKaiOntologyPayloadMappingException.class, 
            OntologyDataParsingException.class, 
            OntologyDataPropertyGraphModellingException.class, 
            WebProtegeMissingCredentials.class, 
            WebProtegeAuthenticationException.class})
    public void springHandleCreationException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
    @ExceptionHandler({OntologyCreationAlreadyExistsException.class})
    public void springHandleAlreadyExistsException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.CONFLICT.value());
    }
    
    @ExceptionHandler({InvalidSparqlQueryException.class, 
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
        WebProtegeInvalidProjectId.class})
    public void springHandleBadRequest(HttpServletResponse response) 
            throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
    
    @ExceptionHandler({WebProtegeProjectAccessException.class})
    public void springHandleForbidden(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value());
    }

}
