package ai.hyperlearning.ontopop.model.ontokai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * OntoKai Ontology Payload Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class OntoKaiOntologyPayload implements Serializable {

    private static final long serialVersionUID = -7870117418972283488L;
    
    private String iaName;
    private boolean selection;
    private int version;
    private List<OntoKaiOntologyNode> nodes = new ArrayList<>();
    
    public OntoKaiOntologyPayload() {
        
    }

    public OntoKaiOntologyPayload(String iaName, boolean selection, 
            int version, List<OntoKaiOntologyNode> nodes) {
        super();
        this.iaName = iaName;
        this.selection = selection;
        this.version = version;
        this.nodes = nodes;
    }

    public String getIaName() {
        return iaName;
    }

    public void setIaName(String iaName) {
        this.iaName = iaName;
    }

    public boolean isSelection() {
        return selection;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<OntoKaiOntologyNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<OntoKaiOntologyNode> nodes) {
        this.nodes = nodes;
    }
    
    @JsonIgnore
    public Map<Integer, String> generateNodeIdIriMap() {
        Map<Integer, String> nodeIdIriMap = new HashMap<>();
        for ( OntoKaiOntologyNode node : this.nodes ) {
            nodeIdIriMap.put(node.getId(), node.getUrl());
        }
        return nodeIdIriMap;
    }

    @Override
    public String toString() {
        return "OntoKaiOntologyPayload ["
                + "iaName=" + iaName + ", "
                + "selection=" + selection + ", "
                + "version=" + version + ", "
                + "nodes=" + nodes 
                + "]";
    }

}
