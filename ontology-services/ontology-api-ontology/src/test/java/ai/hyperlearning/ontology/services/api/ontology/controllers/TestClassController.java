package ai.hyperlearning.ontology.services.api.ontology.controllers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ai.hyperlearning.ontology.services.api.ontology.controllers.ClassController;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlClass;

/**
 * Node Controller Unit Tests
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@SuppressWarnings("unused")
@AutoConfigureMockMvc
@ContextConfiguration(classes = {ClassController.class})
@WebMvcTest(value = ClassController.class)
public class TestClassController {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ClassController classController;
	
	@Disabled("Disabled until mock unauthorized errors resolved")
	@Test
	public void testGetClass() throws Exception {
		
		// Create a mock OWL class object
		RDFOwlClass rdfOwlClass = new RDFOwlClass();
		rdfOwlClass.setRdfAbout(
				"http://webprotege.stanford.edu/R0jI731hv09ZcJeji1fbtY");
		rdfOwlClass.setRdfsLabel("Communication Document");
		Mockito.when(classController.getClassById(
				Mockito.anyInt())).thenReturn(rdfOwlClass);

		// Simulate a GET request and test the response
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/api/ontology/classes/1").accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expectedResponse = "{\"nodeId\":0,\"rdfAbout\":\"http://webprotege.stanford.edu/R0jI731hv09ZcJeji1fbtY\",\"rdfsLabel\":\"Communication Document\",\"owlAnnotationProperties\":null,\"rdfsSubClassOf\":null}";
		JSONAssert.assertEquals(expectedResponse, result.getResponse()
				.getContentAsString(), false);
		
	}

}
