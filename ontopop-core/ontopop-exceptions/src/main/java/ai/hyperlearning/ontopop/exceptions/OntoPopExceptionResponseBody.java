package ai.hyperlearning.ontopop.exceptions;

import java.io.Serializable;

/**
 * OntoPop Exception Response Body Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntoPopExceptionResponseBody implements Serializable {

    private static final long serialVersionUID = -6791529013463816400L;
    private String errorDisplayMessage;
    
    public OntoPopExceptionResponseBody(String errorDisplayMessage) {
        this.errorDisplayMessage = errorDisplayMessage;
    }

    public String getErrorDisplayMessage() {
        return errorDisplayMessage;
    }

    @Override
    public String toString() {
        return "OntoPopExceptionResponseBody ["
                + "errorDisplayMessage=" + errorDisplayMessage 
                + "]";
    }

}
