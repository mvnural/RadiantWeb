package edu.uga.cs.wstool.parser.sawsdl;

import org.jdom.Element;
import org.semanticweb.owlapi.model.IRI;

public class SimpleTypeOBJ implements AnnotationOBJ {

	/** wsdl identifier
     */
	private int id;
	
	/** input/output jdom element for annotation
     */
	private Element element;
	
	/** input/output parameter
     */
    private String name;
    
    /** concept IRI of ontology
     */
    private IRI modelReference;
    
    /** simple type lowering schema mapping IRI of ontology
     */
	private IRI loweringSchemaMapping;
	
	/** simple type lifting schema mapping IRI of ontology
     */
	private IRI liftingSchemaMapping;
    
    /** input/output description
     */
    private String description;
    
    /** input/output type
     */
    private String type;
    
    /** input/output required
     */
    private boolean required = true;

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

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isRequired() {
		return required;
	}

	@Override
	public void setElement(Element element) {
		this.element = element;
	}

	@Override
	public Element getElement() {
		return element;
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
