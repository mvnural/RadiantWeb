package edu.uga.radiant.ontology;

import java.util.ArrayList;
import java.util.Iterator;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLRestriction;

public class Triple {
	
	private OWLClass parent;
	
	@SuppressWarnings("rawtypes")
	private OWLProperty property;
	
	private OWLClass child;
	
	@SuppressWarnings("rawtypes")
	public Triple(OWLClassExpression express, OWLClass _parent){
		
		ArrayList<String> classTypeNames = new ArrayList<String>();
		
		// define classtype name
        classTypeNames.add("objectmaxcardinality");
        classTypeNames.add("objectexactcardinality");
        classTypeNames.add("objectmincardinality");
        classTypeNames.add("ObjectSomeValuesFrom");
        classTypeNames.add("ObjectHasValue");
        classTypeNames.add("ObjectAllValuesFrom");
        
        parent = _parent;
		
        if (express.isAnonymous()) {
            for (int i = 0; i < classTypeNames.size(); i++) {
                if (express.getClassExpressionType().getName().equalsIgnoreCase(classTypeNames.get(i))) {
                	
                    property = ((OWLObjectProperty) ((OWLRestriction) express).getProperty());

                    //check nested ObjectIntersectionOf
                    OWLClassExpression nextRestriction = Restriction.seekIntersectionOrUnion(express);
                    //out.println("nextRestriction = " + nextRestriction);

                    if (nextRestriction == null) {
                        Iterator<OWLClassExpression> childPart = express.getNestedClassExpressions().iterator();
                        while (childPart.hasNext()) {
                            OWLClassExpression _childPart = childPart.next();
                            if (!_childPart.isAnonymous()) {
                            	child = _childPart.asOWLClass();
                            }
                        }
                    }
                }
            } // for
        }
	}
	
	public Triple(){
		// no implement
	}
	
	public void setParent(OWLClass parent) {
		this.parent = parent;
	}
	
	public OWLClass getParent() {
		return parent;
	}

	@SuppressWarnings("rawtypes")
	public void setProperty(OWLProperty property) {
		this.property = property;
	}

	@SuppressWarnings("rawtypes")
	public OWLProperty getProperty() {
		return property;
	}


	public void setChild(OWLClass child) {
		this.child = child;
	}


	public OWLClass getChild() {
		return child;
	}
	
}
