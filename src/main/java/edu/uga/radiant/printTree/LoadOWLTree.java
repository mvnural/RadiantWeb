package edu.uga.radiant.printTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uga.radiantweb.freemarker.model.OntologyNode;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import edu.uga.radiant.ontology.OntologyManager;

public class LoadOWLTree {

	final static String CLASSIMG = "";
	private static int count = 0;

    public static OntologyNode constructFreemarkerModel(OntologyManager manager){
        OWLClass thing = manager.getThing();
        OntologyNode rootNode = new OntologyNode();
        rootNode.setIRI("Thing");
        rootNode.setValue("Thing");
        rootNode.setChildren(getNodeChildren(thing, manager));

        return rootNode;
    }

    private static List<OntologyNode> getNodeChildren(OWLClass parent, OntologyManager manager) {
        List<OntologyNode> children = new ArrayList<OntologyNode>();
        for (OWLClass child : manager.getDirectSubClasses(parent)){
            String value = manager.getClassLabel(child);
            if (!value.equals("Nothing")){ // Do not consider owl:Nothing for the tree if it exists
                OntologyNode childNode = new OntologyNode();
                childNode.setIRI(child.getIRI().toString());
                childNode.setDefinition(manager.getClassDefinition(child));
                childNode.setValue(value);
                childNode.setSpanId("spanidowlclass" + (++count));
                childNode.setIRIFragment(charReplace(child.getIRI().toString()));
                childNode.setChildren(getNodeChildren(child, manager));
                children.add(childNode);
            }
        }

        return children;
    }

    public static String charReplace(String iri) {
        return iri.replace('.', '_').replace('/', '_').replace(':', '_').replace('#', '_');
    }

 /*   public static void drawTree(StringBuffer buf, OntologyManager mgr) throws IOException {
		OWLClass thing = mgr.getThing();
//		buf.append("<div id=\"root\"><ul><li id='Thing'><span>"+CLASSIMG+"Thing</span>");
//		buf.append("<ul>");
		if (thing == null){
			Set<OWLClass> roots = getTopClasses(mgr);
            for (OWLClass child : roots){
                printHierarchy(child, buf, mgr);
            }

		}else{
			printHierarchy(thing, buf, mgr);
		}
//		buf.append("</ul>");
//		buf.append("</li></ul></div>");
	}
*/
	public static Set<OWLClass> getTopClasses(OntologyManager mgr) {
		Set<OWLClass> subclasses = new HashSet<OWLClass>();
        for ( OWLClass cls : mgr.getAllOWLclass().values()){
            for ( OWLClass sbcls : mgr.getDirectSubClasses(cls)){
            	subclasses.add(sbcls);
            }
        }
        Set<OWLClass> classes = new HashSet<OWLClass>();
        classes.addAll(mgr.getAllOWLclass().values());
        classes.removeAll(subclasses);
        return classes;
	}
	
	public static void main(String[] args) throws IOException, OWLOntologyCreationException {
		
		OntologyManager mgr = OntologyManager.getInstance("D:/SUMO_Finance.owl");
		
		System.out.println("mgr class size = " + mgr.getAllOWLclass().keySet().size());
		
		OntologyManager mgr1 = OntologyManager.getInstance("D:/webService.owl");
		
		System.out.println("mgr1 class size = " + mgr1.getAllOWLclass().keySet().size());
		
		OntologyManager mgr2 = OntologyManager.getInstance("D:/SUMO_Finance.owl");
		
		System.out.println("mgr2 class size = " + mgr2.getAllOWLclass().keySet().size());
		
		
	}
	
}
