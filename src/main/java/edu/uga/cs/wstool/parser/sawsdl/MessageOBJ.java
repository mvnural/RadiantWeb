package edu.uga.cs.wstool.parser.sawsdl;

import org.jdom.*;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;

public class MessageOBJ implements AnnotationOBJ {

	/** wsdl identifier
     */
	private int id;
	
	/** wsdl message name
     */
	private String name;
	
	/** element(jdom) for annotation
     */
	private Element element;
	
	/** message concept IRI of ontology
     */
	private IRI modelReference;
	
	/** message lowering schema mapping IRI of ontology
     */
	private IRI loweringSchemaMapping;
	
	/** message lifting schema mapping IRI of ontology
     */
	private IRI liftingSchemaMapping;
	
	/** the list of complex types in this message
     */
	private List<ComplexTypeOBJ> complextype = new ArrayList<ComplexTypeOBJ>();

	/** the list of simple types in this message
     */
	private List<SimpleTypeOBJ> simpletype = new ArrayList<SimpleTypeOBJ>();
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setComplextype(List<ComplexTypeOBJ> complextype) {
		this.complextype = complextype;
	}

	public List<ComplexTypeOBJ> getComplextype() {
		return complextype;
	}

	public void setSimpletype(List<SimpleTypeOBJ> simpletype) {
		this.simpletype = simpletype;
	}

	public List<SimpleTypeOBJ> getSimpletype() {
		return simpletype;
	}

	@Override
	public void setElement(Element element) {
		this.element = element;
	}

	@Override
	public Element getElement() {
		return element;
	}

	public void setLiftingSchemaMapping(IRI liftingSchemaMapping) {
		this.liftingSchemaMapping = liftingSchemaMapping;
	}

	public IRI getLiftingSchemaMapping() {
		return liftingSchemaMapping;
	}

	public void setModelReference(IRI modelReference) {
		this.modelReference = modelReference;
	}

	public IRI getModelReference() {
		return modelReference;
	}

	public void setLoweringSchemaMapping(IRI loweringSchemaMapping) {
		this.loweringSchemaMapping = loweringSchemaMapping;
	}

	public IRI getLoweringSchemaMapping() {
		return loweringSchemaMapping;
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
		setModelReference(SemanticConceptIRI);
	}

	@Override
	public IRI getSemanticConcept() {
		return getModelReference();
	}
	
}
