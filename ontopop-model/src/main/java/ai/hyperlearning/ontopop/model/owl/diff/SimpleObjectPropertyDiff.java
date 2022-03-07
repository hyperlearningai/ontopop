package ai.hyperlearning.ontopop.model.owl.diff;

import java.io.Serializable;

import ai.hyperlearning.ontopop.model.owl.SimpleObjectProperty;

/**
 * Simple OWL Model - Object Property Diff
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleObjectPropertyDiff implements Serializable {

    private static final long serialVersionUID = -6812668116614732848L;
    
    private SimpleObjectProperty before;
    private String beforeXml;
    private SimpleObjectProperty after;
    private String afterXml;
    
    public SimpleObjectPropertyDiff() {
        
    }
    
    public SimpleObjectPropertyDiff(
            SimpleObjectProperty simpleObjectProperty, 
            boolean before) {
        if (before)
            this.before = simpleObjectProperty;
        else
            this.after = simpleObjectProperty;
    }
    
    public SimpleObjectPropertyDiff(
            SimpleObjectProperty before, 
            SimpleObjectProperty after) {
        this.before = before;
        this.after = after;
    }

    public SimpleObjectPropertyDiff(SimpleObjectProperty before,
            String beforeXml, SimpleObjectProperty after, String afterXml) {
        this.before = before;
        this.beforeXml = beforeXml;
        this.after = after;
        this.afterXml = afterXml;
    }

    public SimpleObjectProperty getBefore() {
        return before;
    }

    public void setBefore(SimpleObjectProperty before) {
        this.before = before;
    }

    public String getBeforeXml() {
        return beforeXml;
    }

    public void setBeforeXml(String beforeXml) {
        this.beforeXml = beforeXml;
    }

    public SimpleObjectProperty getAfter() {
        return after;
    }

    public void setAfter(SimpleObjectProperty after) {
        this.after = after;
    }

    public String getAfterXml() {
        return afterXml;
    }

    public void setAfterXml(String afterXml) {
        this.afterXml = afterXml;
    }

    @Override
    public String toString() {
        return "SimpleObjectPropertyDiff ["
                + "before=" + before + ", "
                + "beforeXml=" + beforeXml + ", "
                + "after=" + after + ", "
                + "afterXml=" + afterXml
                + "]";
    }
    
}
