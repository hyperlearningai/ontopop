package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Custom Exception - Ontology Mapping Request
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyMapperInvalidRequestException extends OntoPopException {

    private static final long serialVersionUID = 494276030754015948L;
    private static final String CLASS_NAME = 
            OntologyMapperInvalidRequestException.class.getSimpleName();
    
    public enum ErrorKey {
        
        INVALID_ONTOLOGY_DATA_FILE_BLANK("InvalidOntologyDataFileBlank"), 
        INVALID_ONTOLOGY_DATA_FILE_DOES_NOT_EXIST("InvalidOntologyDataFileDoesNotExist"), 
        INVALID_ONTOLOGY_DATA_FILE_EXTENSION("InvalidOntologyDataFileExtension"), 
        INVALID_ONTOLOGY_DATA_FILE_MIME_TYPE("InvalidOntologyDataFileMimeType"), 
        INVALID_ONTOLOGY_DATA_FILE_SEMANTICS("InvalidOntologyDataFileSemantics"), 
        INVALID_ONTOLOGY_DATA_FILE_SIZE("InvalidOntologyDataFileSize"), 
        INVALID_SOURCE_FORMAT("InvalidSourceFormat"), 
        INVALID_TARGET_FORMAT("InvalidTargetFormat");
        
        private final String key;
        
        private ErrorKey(String key) {
            this.key = key;
        }
        
        public String getKey() {
            return key;
        }
        
    }

    public OntologyMapperInvalidRequestException() {
        super(CLASS_NAME);
    }
    
    public OntologyMapperInvalidRequestException(ErrorKey errorKey) {
        super(CLASS_NAME, errorKey.getKey());
    }

}
