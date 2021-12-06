package ai.hyperlearning.ontopop.data.jpa.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.ontology.OntologyNonSecretData;

/**
 * Ontology Mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Mapper(componentModel = "spring")
public interface OntologyMapper {
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	void updateOntology(OntologyNonSecretData ontologyNonSecretData, @MappingTarget Ontology ontology);

}
