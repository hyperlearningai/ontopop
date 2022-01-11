package ai.hyperlearning.ontopop.triplestore.jena;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import ai.hyperlearning.ontopop.triplestore.TriplestoreService;
import reactor.core.publisher.Mono;

/**
 * Apache Jena Triplestore Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value="storage.triplestore.service", 
        havingValue = "apache-jena")
public class JenaTriplestoreService implements TriplestoreService {

	private static final String FUSEKI_DATASETS_ENDPOINT = "/$/datasets";
	private static final String FUSEKI_DATASET_ENDPOINT = "/$/datasets/{name}";
	private static final String FUSEKI_DATASET_UPLOAD_DATA_ENDPOINT = "/{name}/data";
	private static final String FUSEKI_DATASET_NAME_KEY = "dbName";
	private static final String FUSEKI_DATASET_TYPE_KEY = "dbType";
	private static final String FUSEKI_DATASET_TYPE_VALUE = "tdb2";
	
	@Autowired
	@Qualifier("fusekiWebClient")
	private WebClient webClient;
	
	@Override
	public ResponseEntity<String> getRepository(int id) throws IOException {
		
		// Send a HTTP GET request to the Fuskei server to get a dataset
		ResponseEntity<String> response = webClient.get()
				.uri(FUSEKI_DATASET_ENDPOINT, String.valueOf(id))
				.retrieve()
				.onStatus(
						status -> status.value() != HttpStatus.OK_200,
				        clientResponse -> Mono.empty())
				.toEntity(String.class)
				.block();
		
		// Return NULL if the response status code is 404 Not Found
		if ( response == null )
			throw new IOException("Null HTTP Response");
		if ( response.getStatusCodeValue() == HttpStatus.NOT_FOUND_404 )
			return null;
		else return response;
		
	}

	@Override
	public void createRepository(int id) throws IOException {
		
		// Prepare the form data
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add(FUSEKI_DATASET_NAME_KEY, String.valueOf(id));
		formData.add(FUSEKI_DATASET_TYPE_KEY, FUSEKI_DATASET_TYPE_VALUE);
		
		// Send a HTTP POST request to the Fuskei server to create a dataset
		ResponseEntity<String> response = webClient.post()
				.uri(FUSEKI_DATASETS_ENDPOINT)
				.header(HttpHeaders.CONTENT_TYPE, 
						MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(formData))
				.retrieve()
				.onStatus(
						status -> status.value() != HttpStatus.OK_200,
						clientResponse -> Mono.empty())
				.toEntity(String.class)
				.block();
		
		// Throw an exception if the HTTP response status is not 200 OK
		if ( response == null )
			throw new IOException(
					"Null HTTP Response");
		if ( response.getStatusCodeValue() != HttpStatus.OK_200 )
			throw new IOException(
					"Invalid HTTP Response " + response.getStatusCodeValue());
		
	}

	@Override
	public void deleteRepository(int id) throws IOException {
		
		// Send a HTTP DELETE request to the Fuskei server to delete a dataset
		ResponseEntity<String> response = webClient.delete()
				.uri(FUSEKI_DATASET_ENDPOINT, String.valueOf(id))
				.retrieve()
				.onStatus(
						status -> status.value() != HttpStatus.OK_200,
				        clientResponse -> Mono.empty())
				.toEntity(String.class)
				.block();
		
		// Throw an exception if the HTTP response status is not 200 OK
		if ( response == null )
			throw new IOException(
					"Null HTTP Response");
		if ( response.getStatusCodeValue() != HttpStatus.OK_200 )
			throw new IOException(
					"Invalid HTTP Response " + response.getStatusCodeValue());
		
	}

	@Override
	public void loadOntologyOwlRdfXml(int id, String owlSourceUri) 
			throws IOException {
		
		// Create the dataset if it does not already exist
		if ( getRepository(id) == null )
			createRepository(id);
		
		// Build the multipart file
		final MultipartBodyBuilder builder = new MultipartBodyBuilder();
		File file = new File(owlSourceUri);
		try (FileInputStream input = new FileInputStream(file)) {
			MultipartFile multipartFile = new MockMultipartFile("file",
		            file.getName(), "text/plain", IOUtils.toByteArray(input));
			builder.part("file", multipartFile.getResource());
		}
		
		// Send a HTTP PUT request to the Fuskei server to upload data
		ResponseEntity<String> response = webClient.put()
				.uri(FUSEKI_DATASET_UPLOAD_DATA_ENDPOINT, String.valueOf(id))
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(builder.build()))
				.retrieve()
				.onStatus(
						status -> status.value() != HttpStatus.OK_200,
						clientResponse -> Mono.empty())
				.toEntity(String.class)
				.block();
		
		// Throw an exception if the HTTP response status is not 200 OK
		if ( response == null )
			throw new IOException(
					"Null HTTP Response");
		if ( response.getStatusCodeValue() != HttpStatus.OK_200 )
			throw new IOException(
					"Invalid HTTP Response " + response.getStatusCodeValue());
		
	}

	@Override
	public void cleanup() throws IOException {
		
	}

}
