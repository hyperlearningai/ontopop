package ai.hyperlearning.ontopop.model.owl.diff;

import java.io.Serializable;

import ai.hyperlearning.ontopop.model.owl.SimpleClass;

/**
 * Simple OWL Model - Class Diff
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleClassDiff implements Serializable {

    private static final long serialVersionUID = 6627411071149576519L;
    
    private SimpleClass before;
    private String beforeXml;
    private SimpleClass after;
    private String afterXml;
    
    public SimpleClassDiff() {
        
    }

    public SimpleClassDiff(SimpleClass before, String beforeXml,
            SimpleClass after, String afterXml) {
        this.before = before;
        this.beforeXml = beforeXml;
        this.after = after;
        this.afterXml = afterXml;
    }

    public SimpleClass getBefore() {
        return before;
    }

    public void setBefore(SimpleClass before) {
        this.before = before;
    }

    public String getBeforeXml() {
        return beforeXml;
    }

    public void setBeforeXml(String beforeXml) {
        this.beforeXml = beforeXml;
    }

    public SimpleClass getAfter() {
        return after;
    }

    public void setAfter(SimpleClass after) {
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
        return "SimpleClassDiff ["
                + "before=" + before + ", "
                + "beforeXml=" + beforeXml + ", "
                + "after=" + after + ", "
                + "afterXml=" + afterXml 
                + "]";
    }
    
}
