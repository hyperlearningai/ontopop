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
    private int errorCode;
    private String errorKey;
    private String errorMessage;
    
    public OntoPopExceptionResponseBody(String errorKey, 
            String errorMessage) {
        int pipeIndex = errorMessage.indexOf('|');
        this.errorCode = (pipeIndex > -1) ?
                Integer.parseInt(errorMessage.substring(0, pipeIndex))
                    : 3001;
        this.errorKey = errorKey;
        this.errorMessage = (pipeIndex > -1) ?
                errorMessage.substring(pipeIndex + 1) : errorMessage ;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public String getErrorKey() {
        return errorKey;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "OntoPopExceptionResponseBody ["
                + "errorCode=" + errorCode + ", "
                + "errorKey=" + errorKey + ", "
                + "errorMessage=" + errorMessage 
                + "]";
    }

}
