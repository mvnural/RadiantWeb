package edu.uga.radiant.suggestion;

public class SuggestionOBJ {
	
	private String conceptIRI;
	private String conceptLabel;
	private String conceptDoc;
	private double score;
	
	public void setConceptIRI(String conceptIRI) {
		this.conceptIRI = conceptIRI;
	}
	
	public String getConceptIRI() {
		return conceptIRI;
	}
	
	public void setConceptLabel(String conceptLabel) {
		this.conceptLabel = conceptLabel;
	}
	
	public String getConceptLabel() {
		return conceptLabel;
	}

	public void setConceptDoc(String conceptDoc) {
		this.conceptDoc = conceptDoc;
	}

	public String getConceptDoc() {
		return conceptDoc;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return score;
	}

}
