package edu.uga.radiantweb.freemarker.model;

import java.util.List;

/**
 * Created by mnural on 12/8/13.
 */
public class OntologyNode {
    private String spanId;
    private String IRI;
    private String definition;
    private String IRIFragment;

    private String value;

    private List<OntologyNode> children;

    public OntologyNode() {
        this.spanId = "";
        this.IRI = "";
        this.definition = "";
        this.IRIFragment = "";
        this.value = "";
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getIRI() {
        return this.IRI;
    }

    public void setIRI(String IRI) {
        this.IRI = IRI;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        if(definition != null){
            definition = definition.replaceAll("\n", "");
            definition = definition.replaceAll("'", "");
            this.definition = definition;
        }

    }

    public String getIRIFragment() {
        return IRIFragment;
    }

    public void setIRIFragment(String IRIFragment) {
        this.IRIFragment = IRIFragment;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<OntologyNode> getChildren() {
        return children;
    }

    public void setChildren(List<OntologyNode> children) {
        this.children = children;
    }
}
