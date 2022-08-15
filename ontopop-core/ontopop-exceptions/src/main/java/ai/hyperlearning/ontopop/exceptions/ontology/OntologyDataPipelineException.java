package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Custom Exception - Ontology Data Pipeline Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDataPipelineException extends OntoPopException {

    private static final long serialVersionUID = -1251907528203807267L;
    private static final String CLASS_NAME = 
            OntologyDataPipelineException.class.getSimpleName();
    
    public enum ErrorKey {
        
        EXPORTER_FROM_WEBPROTEGE("ExporterFromWebProtege"), 
        EXPORTER_TO_GIT("ExporterToGit"), 
        INDEXER_GRAPH("IndexerGraph"), 
        INGESTOR("Ingestor"), 
        LOADER_GRAPH("LoaderGraph"), 
        LOADER_TRIPLESTORE("LoaderTriplestore"), 
        MODELLER_GRAPH("ModellerGraph"), 
        PARSER("Parser"), 
        VALIDATOR("Validator");
        
        private final String key;
        
        private ErrorKey(String key) {
            this.key = key;
        }
        
        public String getKey() {
            return key;
        }
        
    }

    public OntologyDataPipelineException() {
        super(CLASS_NAME);
    }
    
    public OntologyDataPipelineException(ErrorKey errorKey) {
        super(CLASS_NAME, errorKey.getKey());
    }

}
