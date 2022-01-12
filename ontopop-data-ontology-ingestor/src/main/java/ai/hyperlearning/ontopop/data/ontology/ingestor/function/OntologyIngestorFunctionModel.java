package ai.hyperlearning.ontopop.data.ontology.ingestor.function;

import java.io.Serializable;
import java.util.Map;

/**
 * Ontology Ingestor Cloud Function - Function Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyIngestorFunctionModel implements Serializable {

    private static final long serialVersionUID = -4731096845791529487L;
    private Map<String, String> headers;
    private String payload;

    public OntologyIngestorFunctionModel() {

    }

    public OntologyIngestorFunctionModel(Map<String, String> headers,
            String payload) {
        super();
        this.headers = headers;
        this.payload = payload;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "OntologyIngestorFunctionModel [" 
                + "headers=" + headers + ", "
                + "payload=" + payload + 
                "]";
    }

}
