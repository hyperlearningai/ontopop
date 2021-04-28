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

import ai.hyperlearning.ontology.services.api.ontology.controllers.PropertyController;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlAnnotationProperty;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlObjectProperty;

/**
 * Property Controller Unit Tests
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@SuppressWarnings("unused")
@AutoConfigureMockMvc
@ContextConfiguration(classes = {PropertyController.class})
@WebMvcTest(value = PropertyController.class)
public class TestPropertyController {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private PropertyController propertyController;
	
	@Disabled("Disabled until mock unauthorized errors resolved")
	@Test
	public void testGetAnnotationProperty() throws Exception {
		
		// Create a mock annotation property object
		RDFOwlAnnotationProperty rdfOwlAnnotationProperty = 
				new RDFOwlAnnotationProperty();
		rdfOwlAnnotationProperty.setId(1);
		rdfOwlAnnotationProperty.setRdfAboutManually(
				"http://webprotege.stanford.edu/R8AWk6f00nQhiAoDl6ujohI");
		rdfOwlAnnotationProperty.setRdfsLabelManually("Subdomain");
		rdfOwlAnnotationProperty.setSkosDefinitionManually(
				"Annotation used to describe the Entities that compose a "
				+ "specific Subdomain.");
		rdfOwlAnnotationProperty.setSkosCommentManually("The list of existing "
				+ "Subdomains can be found in Project/Tags.");
		Mockito.when(propertyController.getAnnotationPropertyById(
				Mockito.anyInt())).thenReturn(rdfOwlAnnotationProperty);
		
		// Simulate a GET request and test the response
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/api/ontology/properties/annotations/1")
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expectedResponse = "{\"id\":1,\"rdfAbout\":\"http://webprotege.stanford.edu/R8AWk6f00nQhiAoDl6ujohI\",\"rdfsLabel\":\"Subdomain\",\"skosComment\":\"The list of existing Subdomains can be found in Project/Tags.\",\"skosDefinition\":\"Annotation used to describe the Entities that compose a specific Subdomain.\"}";
		JSONAssert.assertEquals(expectedResponse, result.getResponse()
				.getContentAsString(), false);
		
	}
	
	@Disabled("Disabled until mock unauthorized errors resolved")
	@Test
	public void testGetObjectProperty() throws Exception {
		
		// Create a mock object property object
		RDFOwlObjectProperty rdfOwlObjectProperty = new RDFOwlObjectProperty();
		rdfOwlObjectProperty.setId(1);
		rdfOwlObjectProperty.setRdfAbout(
				"http://webprotege.stanford.edu/R4I2v4Y7su3Adf0Vcj6TWd");
		rdfOwlObjectProperty.setRdfsLabel("Proposed in");
		rdfOwlObjectProperty.setSkosDefinition("Relationship used to specify "
				+ "the stage, document or place where an Entity is offered or "
				+ "suggested for consideration, acceptance, or action.");
		rdfOwlObjectProperty.setSkosComment(null);
		Mockito.when(propertyController.getObjectPropertyById(
				Mockito.anyInt())).thenReturn(rdfOwlObjectProperty);
		
		// Simulate a GET request and test the response
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/api/ontology/properties/objects/1")
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expectedResponse = "{\"id\":1,\"rdfAbout\":\"http://webprotege.stanford.edu/R4I2v4Y7su3Adf0Vcj6TWd\",\"rdfsLabel\":\"Proposed in\",\"skosDefinition\":\"Relationship used to specify the stage, document or place where an Entity is offered or suggested for consideration, acceptance, or action.\",\"skosComment\":null,\"owlAnnotationProperties\":null,\"rdfsSubPropertyOf\":null}";
		JSONAssert.assertEquals(expectedResponse, result.getResponse()
				.getContentAsString(), false);
		
	}

}
