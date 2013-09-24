package edu.uga.cs.wstool.parser.sawsdl;

import org.jdom.Element;
import org.semanticweb.owlapi.model.IRI;

/**
 * 
 * @author Long
 * 
 * the interface is represent the super class of all object type which can be annotated. (ex. simple, complex and operation)
 */
public interface AnnotationOBJ {
 
	/**
	 * set the annotation object identifier
	 * @param id annotation object identifier
	 */
	public void setId(int id);

	
	/**
	 * get annotation object identifier
	 * @return annotation object identifier
	 */
	public int getId();
	
	/**
	 * set annotation object name
	 * @param name annotation object name
	 */
	public void setName(String name);

	/**
	 * get annotation object name
	 * @return annotation object name
	 */
	public String getName();
	
	/**
	 * set annotation object's semantic concept iri
	 * @param SemanticConceptIRI annotation object's semantic concept iri
	 */
	public void setSemanticConcept(IRI SemanticConceptIRI);

	/**
	 * get annotation object's semantic concept iri
	 * @return annotation object's semantic concept iri
	 */
	public IRI getSemanticConcept();
	
	/**
	 * set annotation object's jdom's element
	 * @param element annotation object's jdom's element
	 */
	public void setElement(Element element);

	/**
	 * get annotation object's jdom's element
	 * @return annotation object's jdom's element
	 */
	public Element getElement();

}
