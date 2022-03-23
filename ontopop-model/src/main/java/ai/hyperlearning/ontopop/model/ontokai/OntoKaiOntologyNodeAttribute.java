package ai.hyperlearning.ontopop.model.ontokai;

import java.io.Serializable;

/**
 * OntoKai Ontology Node Attribute Schema
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntoKaiOntologyNodeAttribute implements Serializable {

    private static final long serialVersionUID = -7323969363293820359L;
    
    private String name;
    private String value;
    private String dataSource;
    private int json;
    
    public OntoKaiOntologyNodeAttribute() {
        
    }

    public OntoKaiOntologyNodeAttribute(String name, String value,
            String dataSource, int json) {
        super();
        this.name = name;
        this.value = value;
        this.dataSource = dataSource;
        this.json = json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public int getJson() {
        return json;
    }

    public void setJson(int json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "OntoKaiOntologyNodeAttribute ["
                + "name=" + name + ", "
                + "value=" + value + ", "
                + "dataSource=" + dataSource + ", "
                + "json=" + json 
                + "]";
    }

}
