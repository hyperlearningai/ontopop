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
    private String errorKey;
    private String errorDisplayMessage;
    
    public OntoPopExceptionResponseBody(String errorKey, 
            String errorDisplayMessage) {
        this.errorKey = errorKey;
        this.errorDisplayMessage = errorDisplayMessage;
    }
    
    public String getErrorKey() {
        return errorKey;
    }

    public String getErrorDisplayMessage() {
        return errorDisplayMessage;
    }

    @Override
    public String toString() {
        return "OntoPopExceptionResponseBody ["
                + "errorKey=" + errorKey + ", "
                + "errorDisplayMessage=" + errorDisplayMessage 
                + "]";
    }

}
