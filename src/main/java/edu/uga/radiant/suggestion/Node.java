package edu.uga.radiant.suggestion;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * The Node is the object which is under the hierarchy of the complex type.
 * One complex type may have multiple Nodes, These Nodes may be complex type or simple type
 * Every Node has an OWLProperty to represent the relationship between itself and parent.
 * 
 * @author Long
 */
public class Node {
	
	/**
	 * indicate the OWLclass of this node
	 */
	private OWLClass cls;
	
	@SuppressWarnings("rawtypes")
	/**
	 * indicate the relationship of this node and its parent
	 */
	private OWLProperty property;
	
	/**
	 * indicate if the node is complex type
	 */
	private boolean isComplexType;

	/**
	 * set if node is complex type 
	 * @param isComplexType true if it is complex type
	 */
	public void setComplexType(boolean isComplexType) {
		this.isComplexType = isComplexType;
	}

	/**
	 * return if it is complex tpye
	 * @return true if it is complex type
	 */
	public boolean isComplexType() {
		return isComplexType;
	}

	/**
	 * Set the semantic concept OWLClass which represent this node 
	 * @param cls OWLClass which represent the node
	 */
	public void setCls(OWLClass cls) {
		this.cls = cls;
	}

	/**
	 * get OWLClass which represent the node
	 * @return OWLClass which represent the node
	 */
	public OWLClass getCls() {
		return cls;
	}

	@SuppressWarnings("rawtypes")
	/**
 	 * Set the semantic relationship OWLProperty which represent the relationship with its parent 
	 * @param cls OWLClass which represent the node
	 */
	public void setProperty(OWLProperty property) {
		this.property = property;
	}

	@SuppressWarnings("rawtypes")
	/**
	 * Set the semantic relationship OWLProperty which represent the relationship with its parent 
	 * @return semantic relationship OWLProperty which represent the relationship with its parent
	 */
	public OWLProperty getProperty() {
		return property;
	}
	
}
