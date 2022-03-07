package ai.hyperlearning.ontopop.model.owl.diff;

import java.io.Serializable;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;

/**
 * Simple OWL Model - Annotation Property Diff
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleAnnotationPropertyDiff implements Serializable {

    private static final long serialVersionUID = 5415071394062333380L;
    
    private SimpleAnnotationProperty before;
    private String beforeXml;
    private SimpleAnnotationProperty after;
    private String afterXml;
    
    public SimpleAnnotationPropertyDiff() {
        
    }

    public SimpleAnnotationPropertyDiff(
            SimpleAnnotationProperty before, String beforeXml, 
            SimpleAnnotationProperty after, String afterXml 
            ) {
        this.before = before;
        this.beforeXml = beforeXml;
        this.after = after;
        this.afterXml = afterXml;
    }

    @Override
    public String toString() {
        return "SimpleAnnotationPropertyDiff ["
                + "before=" + before + ", "
                + "beforeXml=" + beforeXml + ", "
                + "after=" + after + ", "
                + "afterXml=" + afterXml
                + "]";
    }
    
}
