package edu.uga.cs.wstool.parser.sawsdl;

import org.jdom.Element;
import org.semanticweb.owlapi.model.IRI;

public class OperationOBJ implements AnnotationOBJ {

	/** wsdl identifier
     */
	private int id;
	
	/** element(jdom) for annotation
     */
	private Element element;
	
	/** wsdl operation name
     */
	private String name;
	
	/** operation concept IRI of ontology, describe the functionality
     */
	private IRI functionality;
	
	/** operation document
     */
	private String doc;
	
	/** input message
     */
	private MessageOBJ input;
	
	/** output message
     */
	private MessageOBJ output;

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	public String getDoc() {
		return doc;
	}

	public void setInput(MessageOBJ input) {
		this.input = input;
	}

	public MessageOBJ getInput() {
		return input;
	}

	public void setOutput(MessageOBJ output) {
		this.output = output;
	}

	public MessageOBJ getOutput() {
		return output;
	}

	public void setFunctionality(IRI functionality) {
		this.functionality = functionality;
	}

	public IRI getFunctionality() {
		return functionality;
	}

	@Override
	public void setElement(Element element) {
		this.element = element;
	}

	@Override
	public Element getElement() {
		return element;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setSemanticConcept(IRI SemanticConceptIRI) {
		setFunctionality(SemanticConceptIRI);
	}

	@Override
	public IRI getSemanticConcept() {
		return getFunctionality();
	}
	
}
