package edu.uga.cs.wstool.parser.sawsdl;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.semanticweb.owlapi.model.IRI;

public class ComplexTypeOBJ implements AnnotationOBJ {

	/** wsdl identifier
     */
	private int id;
	
	/** complex type element(jdom) for annotation
     */
	private Element element;
	
	/** concept IRI of ontology
     */
    private IRI modelReference;
    
    /** complex type lowering schema mapping IRI of ontology
     */
	private IRI loweringSchemaMapping;
	
	/** complex type lifting schema mapping IRI of ontology
     */
	private IRI liftingSchemaMapping;
	
	/** complex type name
     */
	private String name;
	
	/** complex type description text
     */
	private String description;
	
	/** nested complex types
     */
	private List<ComplexTypeOBJ> complextypes = new ArrayList<ComplexTypeOBJ>();
	
	/** simple type list of this complex type
     */
	private List<SimpleTypeOBJ> simples = new ArrayList<SimpleTypeOBJ>();

	public void setComplextypes(List<ComplexTypeOBJ> complextypes) {
		this.complextypes = complextypes;
	}

	public List<ComplexTypeOBJ> getComplextypes() {
		return complextypes;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public void setElement(Element element) {
		this.element = element;
	}

	@Override
	public Element getElement() {
		return element;
	}

	public void setSimples(List<SimpleTypeOBJ> simples) {
		this.simples = simples;
	}

	public List<SimpleTypeOBJ> getSimples() {
		return simples;
	}

	public void setModelReference(IRI modelReference) {
		this.modelReference = modelReference;
	}

	public IRI getModelReference() {
		return modelReference;
	}

	public void setLiftingSchemaMapping(IRI liftingSchemaMapping) {
		this.liftingSchemaMapping = liftingSchemaMapping;
	}

	public IRI getLiftingSchemaMapping() {
		return liftingSchemaMapping;
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
