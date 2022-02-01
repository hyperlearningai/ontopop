package ai.hyperlearning.ontopop.security.secrets.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ontology Model - Secret Data
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologySecretData implements Serializable {

    private static final long serialVersionUID = -3107174777469504852L;
    
    @Schema(description = "ID of the ontology to update.", 
            example = "1", 
            required = false)
    private int id;
    
    @Schema(description = "Updated access token required to access the OWL file managed in private Git repositories.", 
            example = "ghp_123456789abcdefghi", 
            required = false)
    private String repoToken;
    
    @Schema(description = "Updated user-defined webhook secret used to validate Git webhooks from this Git repository.", 
            example = "mysecret123", 
            required = false)
    private String repoWebhookSecret;

    public OntologySecretData() {

    }

    public OntologySecretData(int id, String repoToken,
            String repoWebhookSecret) {
        this.id = id;
        this.repoToken = repoToken;
        this.repoWebhookSecret = repoWebhookSecret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRepoToken() {
        return repoToken;
    }

    public void setRepoToken(String repoToken) {
        this.repoToken = repoToken;
    }

    public String getRepoWebhookSecret() {
        return repoWebhookSecret;
    }

    public void setRepoWebhookSecret(String repoWebhookSecret) {
        this.repoWebhookSecret = repoWebhookSecret;
    }

}
