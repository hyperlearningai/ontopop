package ai.hyperlearning.ontology.services.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.util.Formatter;
import java.util.function.Consumer;

import javax.net.ssl.HttpsURLConnection;

/**
 * HTTP Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class HttpUtils {
	
	private final static HttpClient CLIENT = HttpClient.newHttpClient();
	
	/**
	 * Send a HTTP request
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	
	public static HttpResponse<String> sendRequest(HttpRequest request) 
			throws IOException, InterruptedException {
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }
	
	/**
	 * Build URI
	 * @param consumer
	 * @return
	 */
	
	public static URI buildURI(Consumer<Formatter> formatFunction) {
        Formatter strFormatter = new Formatter();
        formatFunction.accept(strFormatter);
        String url = strFormatter.out().toString();
        strFormatter.close();
        return URI.create(url);
	}
	
	/**
	 * Check for a successful response
	 * @param response
	 * @return
	 */
	
	public static boolean isSuccessResponse(HttpResponse<String> response) {
        
		try {
			
            int responseCode = response.statusCode();
            if (responseCode == HttpURLConnection.HTTP_OK 
            		|| responseCode == HttpURLConnection.HTTP_ACCEPTED
                    || responseCode == HttpURLConnection.HTTP_NO_CONTENT 
                    || responseCode == HttpsURLConnection.HTTP_CREATED) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
	
	/**
	 * Build a new HTTP request
	 * @param uri
	 * @param key
	 * @param method
	 * @param contents
	 * @return
	 */
	
	public static HttpRequest httpRequest(URI uri, String key, 
			String method, String contents) {
		
        contents = contents == null ? "" : contents;
        Builder builder = HttpRequest.newBuilder();
        builder.uri(uri);
        builder.setHeader("content-type", "application/json");
        builder.setHeader("api-key", key);

        switch (method) {
            case "GET":
                builder = builder.GET();
                break;
            case "HEAD":
                builder = builder.GET();
                break;
            case "DELETE":
                builder = builder.DELETE();
                break;
            case "PUT":
                builder = builder.PUT(
                		HttpRequest.BodyPublishers.ofString(contents));
                break;
            case "POST":
                builder = builder.POST(
                		HttpRequest.BodyPublishers.ofString(contents));
                break;
            default:
                throw new IllegalArgumentException(String.format(
                		"Can't create request for method '%s'", method));
        }
        
        return builder.build();
        
    }

}
