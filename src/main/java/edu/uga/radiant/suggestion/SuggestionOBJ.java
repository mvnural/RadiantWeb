package edu.uga.radiant.suggestion;

public class SuggestionOBJ {
	
	private String ConceptIRI;
	private String ConceptLabel;
	private String ConceptDoc;
	private double score;
	
	public void setConceptIRI(String conceptIRI) {
		ConceptIRI = conceptIRI;
	}
	
	public String getConceptIRI() {
		return ConceptIRI;
	}
	
	public void setConceptLabel(String conceptLabel) {
		ConceptLabel = conceptLabel;
	}
	
	public String getConceptLabel() {
		return ConceptLabel;
	}

	public void setConceptDoc(String conceptDoc) {
		ConceptDoc = conceptDoc;
	}

	public String getConceptDoc() {
		return ConceptDoc;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return score;
	}

}
