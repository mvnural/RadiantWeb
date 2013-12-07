package edu.uga.radiantweb.freemarker.model;

/**
 * Created by mnural on 12/7/13.
 */
public class OntologySearchResult {


    private String definition;
    private String label;
    private String fragmentData;
    private String score;

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setFragmentData(String fragmentData) {
        this.fragmentData = fragmentData;
    }

    public String getFragmentData() {
        return fragmentData;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScore() {
        return score;
    }
}
