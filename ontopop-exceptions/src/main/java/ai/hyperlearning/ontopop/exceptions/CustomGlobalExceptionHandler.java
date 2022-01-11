package ai.hyperlearning.ontopop.exceptions;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ai.hyperlearning.ontopop.exceptions.git.WebhookEventNotFoundException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyCreationException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyNotFoundException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyUpdateSecretDataException;

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
            WebhookEventNotFoundException.class})
    public void springHandleNotFound(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({OntologyCreationException.class,
            OntologyUpdateSecretDataException.class})
    public void springHandleCreationException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

}
