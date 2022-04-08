package a.hyperlearning.ontopop.api.ontology.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Mapping API Service - Mapping Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/mapping")
@Tag(name = "Mapping API", description = "API for undertaking common mapping operations")
public class OntologyMappingController {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyMappingController.class);
    
    

}
