package ai.hyperlearning.ontopop.cloud.aws.utils.amazonmq;

import java.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;
import net.minidev.json.JSONArray;

/**
 * Amazon MQ/RabbitMQ Helper Functions
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class AmazonMqUtils {
    
    private static final String JSON_PATH_FIRST_MESSAGE_DATA = 
            "$['rmqMessagesByQueue'][<queue>][<messageIndex>]['data']";
    
    /**
     * Get the OntologyMessage object from the Nth message in a given 
     * Amazon MQ (RabbitMQ) queue
     * @param message
     * @param queue
     * @return
     */
    
    public static OntologyMessage getOntologyMessage(
            String message, String queue, int messageIndex) {
        String jsonPath = JSON_PATH_FIRST_MESSAGE_DATA
                .replace("<queue>", "'" + queue + "'")
                .replace("<messageIndex>", String.valueOf(messageIndex));
        JSONArray data = JsonPath.parse(message).read(jsonPath);
        String base64EncodedData = data.get(0).toString();
        return decodeOntologyMessageData(base64EncodedData);
    }
    
    /**
     * Get the OntologyMessage object from the Nth message in the first
     * queue from an Amazon MQ (RabbitMQ) message
     * @param message
     * @return
     */
    
    public static OntologyMessage getOntologyMessage(
            String message, int messageIndex) {
        String jsonPath = JSON_PATH_FIRST_MESSAGE_DATA
                .replace("<queue>", "*")
                .replace("<messageIndex>", String.valueOf(messageIndex));
        JSONArray data = JsonPath.parse(message).read(jsonPath);
        String base64EncodedData = data.get(0).toString();
        return decodeOntologyMessageData(base64EncodedData);
    }
    
    /**
     * Convert a base 64 encoded string into an OntologyMessage object
     * @param base64EncodedData
     * @return
     */
    
    private static OntologyMessage decodeOntologyMessageData(
            String base64EncodedData) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedData);
        String decodedData = new String(decodedBytes);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(decodedData, OntologyMessage.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    /**
     * Get the WebProtegeWebhook object from the Nth message in a given 
     * Amazon MQ (RabbitMQ) queue
     * @param message
     * @param queue
     * @return
     */
    
    public static WebProtegeWebhook getWebProtegeWebhookMessage(
            String message, String queue, int messageIndex) {
        String jsonPath = JSON_PATH_FIRST_MESSAGE_DATA
                .replace("<queue>", "'" + queue + "'")
                .replace("<messageIndex>", String.valueOf(messageIndex));
        JSONArray data = JsonPath.parse(message).read(jsonPath);
        String base64EncodedData = data.get(0).toString();
        return decodeWebProtegeWebhookMessageData(base64EncodedData);
    }
    
    /**
     * Get the WebProtegeWebhook object from the Nth message in the first
     * queue from an Amazon MQ (RabbitMQ) message
     * @param message
     * @return
     */
    
    public static WebProtegeWebhook getWebProtegeWebhookMessage(
            String message, int messageIndex) {
        String jsonPath = JSON_PATH_FIRST_MESSAGE_DATA
                .replace("<queue>", "*")
                .replace("<messageIndex>", String.valueOf(messageIndex));
        JSONArray data = JsonPath.parse(message).read(jsonPath);
        String base64EncodedData = data.get(0).toString();
        return decodeWebProtegeWebhookMessageData(base64EncodedData);
    }
    
    /**
     * Convert a base 64 encoded string into an WebProtegeWebhook object
     * @param base64EncodedData
     * @return
     */
    
    private static WebProtegeWebhook decodeWebProtegeWebhookMessageData(
            String base64EncodedData) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedData);
        String decodedData = new String(decodedBytes);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(decodedData, WebProtegeWebhook.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
