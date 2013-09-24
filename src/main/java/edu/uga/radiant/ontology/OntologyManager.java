package edu.uga.radiant.ontology;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static java.lang.System.*;

import java.util.*;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import edu.uga.radiant.util.RadiantToolConfig;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/***************************************************************************************
 * This class is used to load OWL files or URL's into memory.  Clients of this class
 * must handle the following classes from the OWLAPI: OWLClass, OWLDataProperty and
 * OWLObjectProperty.
 */
public class OntologyManager {

    
	private static Map<String, OntologyManager> ontologyManagerInstances = new LinkedHashMap<String, OntologyManager>();
    
	private static int instanceLimit = 20;
	
    private OWLClass thing;
	
    /**
     * The ontology manager which is used to be load ontology(owl file).
     */
    private OWLOntologyManager manager;
    /**
     * The ontology factory which is used to get objects in ontology.
     */
    //private OWLDataFactory factory;
    
    /**
     * The ontology to be loaded.
     */
    private OWLOntology ontology;
    private Set<OWLOntology> ontologies;
    
    public static OntologyManager getInstance(String owlURI) throws OWLOntologyCreationException, IOException {
        
        OntologyManager manager = null;
        
        if (ontologyManagerInstances.containsKey(owlURI)) {
            manager = ontologyManagerInstances.get(owlURI);
        } else {
        	if (owlURI.indexOf("http:") != -1){
        		manager = new OntologyManager(IRI.create(owlURI));
            }else{
        		manager = new OntologyManager(owlURI);
            }
        	removeOldestInstance();
            ontologyManagerInstances.put(owlURI, manager);
        }
        return manager;        
    }
    public static OntologyManager getInstance(String owlURI, OWLOntology ont) {
		
    	OntologyManager manager = null;
    	
    	if (ontologyManagerInstances.containsKey(owlURI)) {
            manager = ontologyManagerInstances.get(owlURI);
        } else {
            manager = new OntologyManager(ont);
            removeOldestInstance();
			ontologyManagerInstances.put(owlURI, manager);
        }
       
		return manager;
	}
    
    /**
     * if ontologyManagerInstances is over the limit size, then remove the oldest one
     * this is used to prevent to use too much memory
     */
    public static void removeOldestInstance(){
        if (ontologyManagerInstances.size() > instanceLimit) {
            ontologyManagerInstances.remove(ontologyManagerInstances.keySet().iterator().next());
        }
    }
    
    /**
     * The HashMap for increasing the performance.
     */
    private HashMap<IRI, OWLClass> OwlClaz = new HashMap<IRI, OWLClass>();
    private HashMap<String, OWLClass> OwlLabels = new HashMap<String, OWLClass>();
	private HashMap<IRI, ArrayList<Set<OWLClass>>> SuperClaz = new HashMap<IRI, ArrayList<Set<OWLClass>>>();
    private HashMap<IRI, ArrayList<Set<OWLClass>>> SubClaz = new HashMap<IRI, ArrayList<Set<OWLClass>>>();
    private HashMap<IRI, Set<OWLDataProperty>> OwlDataProps = new HashMap<IRI, Set<OWLDataProperty>>();
	private HashMap<IRI, Set<OWLObjectProperty>> OwlObjProps = new HashMap<IRI, Set<OWLObjectProperty>>();
	private HashMap<String, Integer> Cardinality = new HashMap<String, Integer>();
	private HashMap<OWLClass, Set<Triple>> DirectRestrictTriples = new HashMap<OWLClass, Set<Triple>>();
	
    /***********************************************************************************
     * Construct an ontology loader by obtaining a OWLOntologyManager which, as
     * the name suggests, manages ontology. An ontology is unique within an
     * ontology manager. To load multiple copies of an ontology, multiple
     * managers would have to be used. To parse the owl file we need a reasoner
     * to analyze or infer the owl file. We need to construct the reasoner. For
     * owlapi, the reasoner is incomplete. Right now, we use the HermiT
     * reasoner.
     * 
     * @param String:
     *            file path of the owl file.
     * @throws IOException
     */
    public OntologyManager (String filename) throws IOException,
            OWLOntologyCreationException {
    	
    	// Create our ontology manager in the usual way.
    	manager = OWLManager.createOWLOntologyManager();

        // Create our ontology factory in the usual way.
        //factory = manager.getOWLDataFactory();

        // load ontology on local file
    	File inputOntologyFile = null;
    	boolean fileFound = false;
    	try {
    		inputOntologyFile = new File(new URI(filename));
    		if (inputOntologyFile.getTotalSpace() != 0)  fileFound = true;
			} catch (Exception e) {
			e.printStackTrace();
		}
    	if (fileFound == false){
    		inputOntologyFile = new File(filename);
    	}
        
        // Now load the local copy
        System.out.println("Now loading owl file from " + filename);
        
        ontology = manager.loadOntologyFromOntologyDocument(inputOntologyFile);
        ontologies = manager.getOntologies();
        
        // add all OWLClass to OwlClaz HashMap
        Iterator<OWLOntology> ont = ontologies.iterator();
        while (ont.hasNext()) {
        	for (OWLClass cls : ont.next().getClassesInSignature(true)) {
                if (!OwlClaz.containsKey(cls.getIRI())){
                	OwlClaz.put(cls.getIRI(), cls);
                	if (cls.isOWLThing()) thing = cls;
                }
                OwlLabels.put(getClassLabel(cls), cls);
            }
        }
        System.out.println("total class size = " + OwlClaz.size());
	
    } // OntologyManager Constructor
    
    /***********************************************************************************
     * constructor of OntologyManager: load owl from web
     * 
     * @param IRI:
     *            owl IRI.
     * @throws IOException
     */
	public OntologyManager(IRI iri) throws IOException,
			OWLOntologyCreationException {

		// Create our ontology manager in the usual way.
		manager = OWLManager.createOWLOntologyManager();

		// Now load the local copy
		ontology = manager.loadOntologyFromOntologyDocument(iri);
		ontologies = manager.getOntologies();
		
		// add all OWLClass to OwlClaz HashMap
		Iterator<OWLOntology> ont = ontologies.iterator();
		while (ont.hasNext()) {
			for (OWLClass cls : ont.next().getClassesInSignature(true)) {
				if (!OwlClaz.containsKey(cls.getIRI())) {
					OwlClaz.put(cls.getIRI(), cls);
					if (cls.isOWLThing()) thing = cls;
				}
				OwlLabels.put(getClassLabel(cls), cls);
			}
		}
		System.out.println("total class size = " + OwlClaz.size());

	} // OntologyManager Constructor
    
    /***********************************************************************************
     * constructor of OntologyManager
     * 
     * @param file:
     *            owl file.
     * @throws IOException
     */
	public OntologyManager(File file) throws IOException,
			OWLOntologyCreationException {

		// Create our ontology manager in the usual way.
		manager = OWLManager.createOWLOntologyManager();

		// Now load the local copy
		ontology = manager.loadOntologyFromOntologyDocument(file);
		ontologies = manager.getOntologies();
		
		// add all OWLClass to OwlClaz HashMap
		Iterator<OWLOntology> ont = ontologies.iterator();
		while (ont.hasNext()) {
			for (OWLClass cls : ont.next().getClassesInSignature(true)) {
				if (!OwlClaz.containsKey(cls.getIRI())) {
					OwlClaz.put(cls.getIRI(), cls);
					if (cls.isOWLThing()) thing = cls;
				}
				OwlLabels.put(getClassLabel(cls), cls);
			}
		}
		System.out.println("total class size = " + OwlClaz.size());

	} // OntologyManager Constructor
    
	/***********************************************************************************
     * constructor of OntologyManager
     * 
     * @param OWLOntology:
     *            OWLOntology object.
     * @throws IOException
     */
    public OntologyManager(OWLOntology ont) {
    	
    	ont.getOWLOntologyManager().getOntologies();
    	
    	// Now load the local copy
		ontology = ont;
		manager = ontology.getOWLOntologyManager();
		
		ontologies = manager.getOntologies();
		
		// add all OWLClass to OwlClaz HashMap
		Iterator<OWLOntology> onto = ontologies.iterator();
		while (onto.hasNext()) {
			for (OWLClass cls : onto.next().getClassesInSignature(true)) {
				if (!OwlClaz.containsKey(cls.getIRI())) {
					OwlClaz.put(cls.getIRI(), cls);
					if (cls.isOWLThing()) thing = cls;
				}
				OwlLabels.put(getClassLabel(cls), cls);
			}
		}
		System.out.println("total class size = " + OwlClaz.size());
    	
	}
    
    /*****************************************************************************************
     * @return HashMap<IRI, OWLClass>: All classes in ontology
     */
    public HashMap<IRI, OWLClass> getAllOWLclass(){
		return OwlClaz;
	}
    
    /*****************************************************************************************
     * @return HashMap<String, OWLClass>: All labels of classes
     */
    public HashMap<String, OWLClass> getAllClassLabels(){
		return OwlLabels;
	}
    
    public Set<Triple> getRestrictTriples(OWLClass cls){
    	Set<Triple> triples = new HashSet<Triple>();
    	Set<OWLClassExpression> supers = getDirectSuperExpression(cls, this);
	    for (OWLClassExpression express : supers) {
	    	Triple triple = new Triple(express, cls);
	        triples.add(triple);
    	}
		return triples;
    }
    
    
    public Set<Triple> getRestrictTriplesByChild(OWLClass cls){
    	Set<Triple> triples = new HashSet<Triple>();
    	if (getDirectRestrictTriplesByChild(cls) != null) triples.addAll(getDirectRestrictTriplesByChild(cls));
    	// set triple which sub classes inherit
    	Set<Triple> temp = new HashSet<Triple>();
		for (Triple t : triples){
    		for (OWLClass c : flatten(getSubClasses(t.getParent()))){
    			Triple triple = new Triple();
				triple.setChild(cls);
				triple.setProperty(t.getProperty());
				triple.setParent(c);
				temp.add(triple);
    		}
    	}
    	triples.addAll(temp);
    	return triples;
    }
    
    /*****************************************************************************************
     * pull out all of classes' labels 
     * 
     * @return HashMap<String, OWLClass>: All classes in ontology
     */
    public Set<Triple> getDirectRestrictTriplesByChild(OWLClass cls){
    	if (DirectRestrictTriples.size() != 0){
    		return DirectRestrictTriples.get(cls);
		}else{
			for (OWLClass c : OwlClaz.values()){
				Set<OWLClassExpression> supers = getDirectSuperExpression(c, this);
		        for (OWLClassExpression express : supers) {
		            Triple triple = new Triple(express, c);
		            if (DirectRestrictTriples.containsKey(triple.getChild())){
		            	DirectRestrictTriples.get(triple.getChild()).add(triple);
	            	}else{
	            		Set<Triple> temp = new HashSet<Triple>();
	            		temp.add(triple);
	            		DirectRestrictTriples.put(triple.getChild(), temp);
	            	}	
		        }
			}
		}
    	return DirectRestrictTriples.get(cls);
	}
    
	/*****************************************************************************************
     * pull out all of the classes which are referenced in the ontology And
     * extracting the name of the concept form the URI find the ontology class
     * whose name is the given conceptName
     * 
     * @param conceptName
     *            : the concept class for searching
     * @return OWLClass: the class which matches the input name, if not return null
     */
    public OWLClass getConceptClass(String iri) {
        if (iri == null) {
            return null;
        }
        if (OwlClaz.get(IRI.create(iri)) == null) {
            return null;
        } else {
            return OwlClaz.get(IRI.create(iri));
        }
    }
    
    /***********************************************************************************
     * Get the OWL classes intersect each other .
     * 
     * @param class1
     *            : the first OWLclass for checking intersecting.
     * @param class2
     *            : the second OWLclass for checking intersecting.
     * @return boolean: if the classes intersect if it is not an owl nothing
     */
    public boolean areIntersecting(OWLClass class1, OWLClass class2) {
        OWLDataFactoryImpl classfac = new OWLDataFactoryImpl();
        Set<OWLClassExpression> set = new HashSet<OWLClassExpression>();
        set.add(class1);
        set.add(class2);
        OWLObjectIntersectionOf obj = classfac.getOWLObjectIntersectionOf(set);
        OWLAnonymousClassExpression object = (OWLAnonymousClassExpression) obj;
        return !object.isOWLNothing();
    }

    /***********************************************************************************
     * Get the prefix to the ontology in the parser.
     * 
     * @return The prefix.
     */
    public Set<OWLOntology> getOntologies() {
        return ontologies;
    }
    
    /***********************************************************************************
     * Get the prefix to the ontology in the parser.
     * 
     * @return The prefix.
     */
    public OWLOntology getOntology() {
        return ontology;
    }
    
    /***********************************************************************************
     * Get the classes equivalent to the class cls.
     * 
     * @param cls
     *            : the class equivalent to this class.
     * @return Set<OWLClass>: The classes equivalent to the class cls.
     */
    public Set<OWLClass> getEquivalentClasses(OWLClass cls) {
    	HashSet<OWLClass> visited = new HashSet<OWLClass>();
    	Set<OWLClass> equivs = new HashSet<OWLClass>();
        if (cls == null) return equivs;
        equivs = getEquivalentClasses(visited, cls);
        return equivs;
    } // getEquivalentClasses
    
    public Set<OWLClass> getEquivalentClasses(HashSet<OWLClass> visited, OWLClass cls) {
    	visited.add(cls);  	
    	Set<OWLClass> equivs = new HashSet<OWLClass>();
        Iterator<OWLOntology> ont = ontologies.iterator();
        while (ont.hasNext()) {
        	Set<OWLClassExpression> _equivs = cls.getEquivalentClasses(ont.next());
            for (OWLClassExpression cd : _equivs) {
                if (!cd.isAnonymous()) {
                    equivs.add(cd.asOWLClass());
                }
            } // for 
        }
        // recursive search equivs
        int beforeSize = equivs.size();
        Iterator<OWLClass> nextParts = equivs.iterator();
        while (nextParts.hasNext()) {
        	OWLClass nextPart = nextParts.next(); 
        	if (!visited.contains(nextPart)) {
        		equivs.addAll(getEquivalentClasses(visited, nextPart));
        	}
        	if (equivs.size() > beforeSize) {
        		nextParts = equivs.iterator();
        		beforeSize = equivs.size();
        	}
        }
        return equivs;
    } // getEquivalentClasses

    /**
     * Return true if cls2 is equivalent to cls1
     * @param cls1
     * @param cls2
     * @return 
     */
    public boolean hasEquivalentClass(OWLClass cls1, OWLClass cls2) {
        return (getEquivalentClasses(cls1).contains(cls2));
    }
    
    /***********************************************************************************
     * Get the super classes of the class cls.
     * 
     * @param cls
     *            the class for searching super classes.
     * @return Set<OWLClass>: The super classes of the class cls.
     */
    public Set<OWLClass> getDirectSuperClasses(OWLClass cls) {
        Set<OWLClass> supers = new HashSet<OWLClass>();
        if (cls == null) {
            return supers;
        }
        Iterator<OWLOntology> ont = ontologies.iterator();
        while (ont.hasNext()) {
        	Set<OWLClassExpression> _supers = cls.getSuperClasses(ont.next());
            if (_supers != null) {
                for (OWLClassExpression cd : _supers) {
                    if (!cd.isAnonymous()) {
                        supers.add(cd.asOWLClass());
                    }
                } // for 
            }
        }
        return supers;
    } // getDirectSuperClasses
    
    /**
     * Return super OWLRestriction
     * @param cls
     * @param parser 
     * @return Set<OWLClassExpression>: super OWLRestriction
     */
    public static Set<OWLClassExpression> getDirectSuperExpression(OWLClass cls, OntologyManager mgr) {
    	Set<OWLClassExpression> supers = new HashSet<OWLClassExpression>();
        Iterator<OWLOntology> ont = mgr.getOntologies().iterator();
        while (ont.hasNext()) {
        	Set<OWLClassExpression> _supers = cls.getSuperClasses(ont.next());
            for (OWLClassExpression ce : _supers) {
                if (ce.isAnonymous()) {
                    supers.add(ce);
                }
            } // for 
        }	
        return supers;
    } // getDirectSuperExpression
    
    /**
     * Return true if cls1 has cls2 as a super class.
     * @param cls1
     * @param cls2
     * @return 
     */
    public boolean hasSuperClass(OWLClass cls1, OWLClass cls2) {
        return (getSuperClasses(cls1).contains(cls2));
    }

    /***********************************************************************************
     * Get the super classes of the class cls in hierarchy.
     * 
     * @param owlCls
     *            the class for searching super classes.
     * @return ArrayList<Set<OWLClass>>: The super classes of the class owlCls.
     */
    public ArrayList<Set<OWLClass>> getSuperClasses(OWLClass cls) {
    	ArrayList<Set<OWLClass>> outCeptSuperClz = new ArrayList<Set<OWLClass>>();
    	if (cls == null) return outCeptSuperClz;
    	if (SuperClaz.containsKey(cls.getIRI())){
    		return SuperClaz.get(cls.getIRI());
    	}else {
    		// get every super class of outConceptClass
            Set<OWLClass> temp = getDirectSuperClasses(cls);
            while (temp.size() != 0) {
                Set<OWLClass> nextLevel = new HashSet<OWLClass>();
                for (OWLClass owlCls : temp) {
                    Set<OWLClass> temp1 = getDirectSuperClasses(owlCls);
                    if (temp1.size() != 0) {
                        Iterator<OWLClass> iterator = temp1.iterator();
                        while (iterator.hasNext()) {
                            nextLevel.add(iterator.next());
                        }
                    }
                }
                outCeptSuperClz.add(temp);
                temp = nextLevel;
            }
            SuperClaz.put(cls.getIRI(), outCeptSuperClz);
            return outCeptSuperClz;
    	}
    } // getSuperClasses

    /***********************************************************************************
     * Get the sub classes of the class cls.
     * 
     * @param cls
     *            the class for searching sub classes.
     * @return Set<OWLClass>: The sub classes of the class cls.
     */
    public Set<OWLClass> getDirectSubClasses(OWLClass cls) {	
        Set<OWLClass> subs = new HashSet<OWLClass>();
        if (cls == null) {
            return subs;
        }
        Iterator<OWLOntology> ont = ontologies.iterator();
        while (ont.hasNext()) {
        	Set<OWLClassExpression> _sub = cls.getSubClasses(ont.next());
            if (_sub != null) {
                for (OWLClassExpression cd : _sub) {
                    if (!cd.isAnonymous()) {
                        subs.add(cd.asOWLClass());
                    }
                } // for 
            }
        }
        return subs;
    } // getDirectSubClasses

    /***********************************************************************************
     * Get the sub classes of the class cls in hierarchy.
     * 
     * @param owlCls
     *            the class for searching sub classes.
     * @return ArrayList<Set<OWLClass>>: The sub classes of the class owlCls.
     */
    public ArrayList<Set<OWLClass>> getSubClasses(OWLClass cls) {
    	ArrayList<Set<OWLClass>> outCeptSubClz = new ArrayList<Set<OWLClass>>();
        if (cls == null) return outCeptSubClz;
    	if (SubClaz.containsKey(cls.getIRI())){
    		return SubClaz.get(cls.getIRI());
    	}else {
    		// get every super class of outConceptClass
            Set<OWLClass> temp = getDirectSubClasses(cls);
            while (temp.size() != 0) {
                Set<OWLClass> nextLevel = new HashSet<OWLClass>();
                for (OWLClass owlCls : temp) {
                    Set<OWLClass> temp1 = getDirectSubClasses(owlCls);
                    if (temp1.size() != 0) {
                        Iterator<OWLClass> iterator = temp1.iterator();
                        while (iterator.hasNext()) {
                            nextLevel.add(iterator.next());
                        }
                    }
                }
                outCeptSubClz.add(temp);
                temp = nextLevel;
            }
            SubClaz.put(cls.getIRI(), outCeptSubClz);
            return outCeptSubClz;
    	}
    } // getSubClasses
    
    public static Set<OWLClass> flatten(ArrayList<Set<OWLClass>> input){
    	Set<OWLClass> output = new HashSet<OWLClass>();
    	for(Set<OWLClass> set : input){
    		output.addAll(set);
    	}
    	return output;
    }
    
    /***********************************************************************************
     * Get the data properties of the class cls.
     * 
     * @param cls
     *            the class for searching data properties.
     * @return Set<OWLDataProperty>: The data properties of the class cls.
     */
    public Set<OWLDataProperty> getDataProperties(OWLClass cls) {
    	Set<OWLDataProperty> props = new HashSet<OWLDataProperty>();
    	HashSet<OWLClass> visited = new HashSet<OWLClass>();
    	if (cls == null) {
            return props;
        }
        if (OwlDataProps.containsKey(cls.getIRI())){
        	return OwlDataProps.get(cls.getIRI());
    	}else {
    		props = getDataProperties(visited, cls);
    	}
        OwlDataProps.put(cls.getIRI(), props);
        return props;
    } // getDataProperties
    
    public Set<OWLDataProperty> getDataProperties(HashSet<OWLClass> visited, OWLClass cls) {
        // visited pattern
    	visited.add(cls);
        
    	Set<OWLDataProperty> props = new HashSet<OWLDataProperty>();
        if (cls == null) {
            return props;
        }
        Iterator<OWLOntology> ont = ontologies.iterator();
        while (ont.hasNext()) {
        	OWLOntology onto = ont.next();
        	//out.println("ont = " + onto);

        	for (OWLDataProperty p : onto.getDataPropertiesInSignature(true)) {
            	if (p.getDomains(onto).contains(cls)) {
                    props.add(p);
                } // if
        	}
        	
        	ArrayList<Set<OWLClass>> superList = getSuperClasses(cls);
        	// get all super classes's data property by OWLRestriction
        	for (int i = superList.size() - 1; i >= 0; i--){
            	for (OWLClass c : superList.get(i)){
        			if (!visited.contains(c)) {
        				//out.println("start with = " + c);
        				props.addAll(getDataProperties(visited, c));
        			}
        		} // for
        	} // for
			
        	// define OWLRestriction class type name
        	ArrayList<String> classTypeNames = new ArrayList<String>();
        	classTypeNames.add("datamaxcardinality");
        	classTypeNames.add("dataexactcardinality");
        	classTypeNames.add("datamincardinality");
        	classTypeNames.add("dataSomeValuesFrom");
        	classTypeNames.add("dataHasValue");
        	classTypeNames.add("dataAllValuesFrom");
        	
        	// use super restriction to get property
            Set<OWLClassExpression> supers = cls.getSuperClasses(onto);
            props = nestedSeekDataProperty(visited, supers, classTypeNames, cls, props);
            
    		// use equivalent restriction to get property
            Set<OWLClassExpression> equivs = cls.getEquivalentClasses(onto);
            props = nestedSeekDataProperty(visited, equivs, classTypeNames, cls, props);
            
        } // while ontology
    	return props;
    	
    } // getDataProperties
    
    @SuppressWarnings("rawtypes")
	public Set<OWLDataProperty> nestedSeekDataProperty(HashSet<OWLClass> visited, Set<OWLClassExpression> parents, ArrayList<String> classTypeNames, OWLClass cls, Set<OWLDataProperty> props){
    	for (OWLClassExpression parentPart : parents) {
        	if (parentPart.isAnonymous()){
            	boolean nestedExpression = true;
            	ArrayList<OWLClassExpression> parentPartList = new ArrayList<OWLClassExpression>();
            	parentPartList.add(parentPart);
            	ArrayList<OWLClassExpression> nextParentPartList = new ArrayList<OWLClassExpression>();
            	while (nestedExpression){
            		nestedExpression = false;
            		for (OWLClassExpression parentPart1 : parentPartList) {
            			//out.println(num + "parentPart = " + parentPart1);
            			Iterator<OWLClassExpression> childPart = null;
            			childPart = parentPart1.asConjunctSet().iterator();
            			while (childPart.hasNext()) {
                        	OWLClassExpression _childPart = childPart.next();
                        	//out.println(num + "_childPart = " + _childPart);
                        	for (int i = 0; i < classTypeNames.size(); i++){
                        		if (!_childPart.isAnonymous()) {
                        			if (!getSuperClasses(cls).contains(_childPart.asOWLClass())) {
                        				if (!visited.contains(_childPart.asOWLClass())) {
                        					//out.println("start with = " + _childPart.asOWLClass());
                        					props.addAll(getDataProperties(visited, _childPart.asOWLClass()));
                            			}
                        			}break;
                                }
                        		if (_childPart.getClassExpressionType().getName().equalsIgnoreCase(classTypeNames.get(i))) {
                                    props.add((OWLDataProperty) ((OWLRestriction) _childPart).getProperty());
                                    //out.println("Add property = " + (OWLDataProperty) ((OWLRestriction) _childPart).getProperty());
                                    // store cardinality
                                    String propIRI = ((OWLDataProperty) ((OWLRestriction) _childPart).getProperty()).toString();
                                    String key = cls.getIRI().toString() + propIRI.substring(1, propIRI.length() - 1 );
                                    if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("datamaxcardinality")) {
                                    	Cardinality.put(key, ((OWLDataMaxCardinality) _childPart).getCardinality());
                                    } else if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("dataexactcardinality")) {
                                    	Cardinality.put(key, ((OWLDataExactCardinality) _childPart).getCardinality());
                                    } else if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("datamincardinality")) {
                                    	Cardinality.put(key, ((OWLDataMinCardinality) _childPart).getCardinality());
                                    }
                                    // check nested ObjectIntersectionOf
                                    OWLClassExpression nextParent = seekNestedIntersection(_childPart);
                                    //out.println("nextParent = " + nextParent);
                                    if (nextParent != null){
                                    	nestedExpression = true;
                                    	nextParentPartList = new ArrayList<OWLClassExpression>();
                                    	nextParentPartList.add(nextParent);
                                    }
                                    break;
                                }
                        	} // for
                        } // while
            		}
            		parentPartList = nextParentPartList;
            	} // while
        	} // if
        }
    	return props;
    }


    /***********************************************************************************
     * Get the object properties of the class cls.
     * 
     * @param cls
     *            the class for searching object properties.
     * @return Set<OWLObjectProperty>: The object properties of the class cls.
     */
    public Set<OWLObjectProperty> getObjectProperties(OWLClass cls) {
    	Set<OWLObjectProperty> props = new HashSet<OWLObjectProperty>();
    	HashSet<OWLClass> visited = new HashSet<OWLClass>();
    	if (cls == null) {
            return props;
        }
        if (OwlObjProps.containsKey(cls.getIRI())){
        	return OwlObjProps.get(cls.getIRI());
    	}else {
    		props = getObjectProperties(visited, cls);
    	}
        OwlObjProps.put(cls.getIRI(), props);
        return props;
    } // getObjectProperties
    
    public Set<OWLObjectProperty> getObjectProperties(HashSet<OWLClass> visited, OWLClass cls) {
        // visited pattern
    	visited.add(cls);
        
    	//out.println("base on cls = " + cls);
    	
    	Set<OWLObjectProperty> props = new HashSet<OWLObjectProperty>();
        if (cls == null) {
            return props;
        }
        Iterator<OWLOntology> ont = ontologies.iterator();
        ArrayList<Set<OWLClass>> superList = getSuperClasses(cls);
        while (ont.hasNext()) {
        	OWLOntology onto = ont.next();
        	//out.println("ont = " + onto);
        	
        	for (OWLObjectProperty p : onto.getObjectPropertiesInSignature(true)) {
            	if (p.getDomains(onto).contains(cls)) {
                    //out.println("in first round : add p = " + p);
            		props.add(p);
                } // if
        	}

        	// get all super classes's object property
        	for (int i = superList.size() - 1; i >= 0; i--){
            	for (OWLClass c : superList.get(i)){
        			if (!visited.contains(c)) {
        				//out.println(i + " th start with = " + c);
        				props.addAll(getObjectProperties(visited, c));
        			}
        		} // for
        	} // for
			
        	// define OWLRestriction class type name
        	ArrayList<String> classTypeNames = new ArrayList<String>();
        	classTypeNames.add("objectmaxcardinality");
        	classTypeNames.add("objectexactcardinality");
        	classTypeNames.add("objectmincardinality");
        	classTypeNames.add("ObjectSomeValuesFrom");
        	classTypeNames.add("ObjectHasValue");
        	classTypeNames.add("ObjectAllValuesFrom");
        	
        	// use super restriction to get property
            Set<OWLClassExpression> supers = cls.getSuperClasses(onto);
            props = nestedSeekObjectProperty(visited, supers, classTypeNames, cls, props);
            
    		// use equivalent restriction to get property
            Set<OWLClassExpression> equivs = cls.getEquivalentClasses(onto);
            props = nestedSeekObjectProperty(visited, equivs, classTypeNames, cls, props);
            
        } // while ontology
    		
    	return props;
    	
    } // getObjectProperties
    
    @SuppressWarnings("rawtypes")
	public Set<OWLObjectProperty> nestedSeekObjectProperty(HashSet<OWLClass> visited, Set<OWLClassExpression> parents, ArrayList<String> classTypeNames, OWLClass cls, Set<OWLObjectProperty> props){
    	for (OWLClassExpression parentPart : parents) {
        	if (parentPart.isAnonymous()){
            	boolean nestedExpression = true;
            	ArrayList<OWLClassExpression> parentPartList = new ArrayList<OWLClassExpression>();
            	parentPartList.add(parentPart);
            	ArrayList<OWLClassExpression> nextParentPartList = new ArrayList<OWLClassExpression>();
            	while (nestedExpression){
            		nestedExpression = false;
            		for (OWLClassExpression parentPart1 : parentPartList){
            			//out.println("cls = " + cls + ", " + num + "th parentPart = " + parentPart1);
            			Iterator<OWLClassExpression> childPart = null;
            			childPart = parentPart1.asConjunctSet().iterator();
                		while (childPart.hasNext()) {
                        	OWLClassExpression _childPart = childPart.next();
                        	//out.println(num + "th _childPart = " + _childPart);            	
                        	for (int i = 0; i < classTypeNames.size(); i++){
                        		if (!_childPart.isAnonymous()) {
                        			if (!getSuperClasses(cls).contains(_childPart.asOWLClass())) {
                        				if (!visited.contains(_childPart.asOWLClass())) {
                        					//out.println("start with = " + _childPart.asOWLClass());
                        					props.addAll(getObjectProperties(visited, _childPart.asOWLClass()));
                            			}
                        			}
                        			break;
                                }
                        		if (_childPart.getClassExpressionType().getName().equalsIgnoreCase(classTypeNames.get(i))) {
                                    props.add((OWLObjectProperty) ((OWLRestriction) _childPart).getProperty());
                                    //out.println("Add property = " + (OWLObjectProperty) ((OWLRestriction) _childPart).getProperty());
                                    // store cardinality
                                    String propIRI = ((OWLObjectProperty) ((OWLRestriction) _childPart).getProperty()).toString();
                                    String key = cls.getIRI().toString() + propIRI.substring(1, propIRI.length() - 1 );
                                    if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("objectmaxcardinality")) {
                                    	Cardinality.put(key, ((OWLObjectMaxCardinality) _childPart).getCardinality());
                                    } else if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("objectexactcardinality")) {
                                    	Cardinality.put(key, ((OWLObjectExactCardinality) _childPart).getCardinality());
                                    } else if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("objectmincardinality")) {
                                    	Cardinality.put(key, ((OWLObjectMinCardinality) _childPart).getCardinality());
                                    }
                                    //check nested ObjectIntersectionOf
                                    OWLClassExpression nextParent = seekNestedIntersection(_childPart);
                                    //out.println("nextParent = " + nextParent);
                                    if (nextParent != null){
                                    	nestedExpression = true;
                                    	nextParentPartList = new ArrayList<OWLClassExpression>();
                                    	nextParentPartList.add(nextParent);
                                    }
                                    break;
                                }
                        	} // for
                        } // while
            		}
            		parentPartList = nextParentPartList;
            	} // while
        	} // if
        } // for
    	return props;
    }
    

    /***********************************************************************************
     * Check if the object properties are inverse functional
     * 
     * @param prop
     *            : the OWLProperty for checking.
     * @return boolean: if the property is inverse functional if it is not an
     *         owl nothing
     */
    @SuppressWarnings("rawtypes")
    public boolean isInverseFunctional(OWLProperty prop) {
        if (prop.isOWLDataProperty()) {
            return false;
        }
        Iterator<OWLOntology> ont = ontologies.iterator();
        while (ont.hasNext()) {
        	if(prop.asOWLObjectProperty().isInverseFunctional(ont.next()))
        		return true;
        }
        return false;
    }
    
    /***********************************************************************************
     * Check if the property is functional
     * 
     * @param prop
     *            : the OWLProperty for checking.
     * @return boolean: if the property is functional
     */
    @SuppressWarnings("rawtypes")
    public boolean isFunctional(OWLProperty prop) {
    	Iterator<OWLOntology> ont = ontologies.iterator();
        while (ont.hasNext()) {
        	if(prop.isFunctional(ont.next()))
        		return true;
        }
        return false;
    }

    /***********************************************************************************
     * Combination of both the properties set.
     * 
     * @param cls
     *            the class the class for searching all Owl properties.
     * @return Set<OWLProperty>: The set of the class that include the object
     *         property and data property.
     */
    @SuppressWarnings("rawtypes")
    public Set<OWLProperty> getProperties(OWLClass cls) {
        Set<OWLDataProperty> data_prop = this.getDataProperties(cls);
        Set<OWLObjectProperty> obj_prop = this.getObjectProperties(cls);
        Set<OWLProperty> result_set = new HashSet<OWLProperty>();
        result_set.addAll(obj_prop);
        result_set.addAll(data_prop);
        return result_set;
    } // getProperties

    /***********************************************************************************
     * Get the Cardinality of instance of the class cls.
     * 
     * @param cls
     *            the class for searching cardinality.
     * @return Integer: The exact number(Cardinality) of the class.
     */
    @SuppressWarnings("rawtypes")
    public int getCardinality(OWLProperty prop, OWLClass cls) {
        int val = 0;
        if (cls == null || prop == null) {
            return val;
        }
        getProperties(cls);
        String key = cls.getIRI().toString() + prop.getIRI().toString();
        if (Cardinality.containsKey(key)){
    		return Cardinality.get(key);
    	}else {
            return val;
    	}
    } // getCardinality
    
    /***********************************************************************************
     * Print out all of the classes which are referenced in the ontology along
     * with their data and object properties.
     */
    public void printClasses() {

    	Iterator<OWLClass> claz = OwlClaz.values().iterator();
        while (claz.hasNext()) {
        	out.println(claz.next().getIRI());
        }
        //Node<OWLClass> topNode = reasoner.getTopClassNode();
        //print(topNode, reasoner, 0);

    } // printClasses

    /***********************************************************************************
     * Get the domains of the object property
     * 
     * @param prop
     *            the owl properties that is included by the class.
     * @return Set<OWLClass>: The set of the class that include the object
     *         property.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set<OWLClass> getDomains(OWLProperty prop) {
    	Set<OWLClass> owl_class = new HashSet<OWLClass>();
    	if (prop == null) return owl_class;
    	Iterator<OWLOntology> ont = ontologies.iterator();
        while (ont.hasNext()) {
        	Set<OWLClassExpression> owl_desc = prop.getDomains(ont.next());
            for (OWLClassExpression desc : owl_desc) {
                if (!desc.isAnonymous()) {
                    owl_class.add(desc.asOWLClass());
                }
            }
        }
        return owl_class;

    } // getDomains

    /***********************************************************************************
     * Get the range of the object properties
     * 
     * @param prop
     *            the object property that is included by the class.
     * @return Set<OWLClass>: The set of the class that include the object
     *         property.
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<OWLClass> getRanges(OWLProperty prop) {   
        Set<OWLClass> owl_class = new HashSet<OWLClass>();
        if (prop == null) return owl_class;
        Iterator<OWLOntology> ont = ontologies.iterator();
        while (ont.hasNext()) {
        	Set<OWLClassExpression> owl_desc = prop.getRanges(ont.next());
            for (OWLClassExpression desc : owl_desc) {
                if (!desc.isAnonymous()) {
                    owl_class.add(desc.asOWLClass());
                }
            }
        }
        return owl_class;
    } // getRanges
	
	/***********************************************************************************
     * Get the range of the data properties
     * 
     * @param prop
     *            the data property that is included by the class.
     * @return String The set of the class that include the object
     *         property.
     */
	public String getRanges(OWLDataProperty prop) {   
		String dataType = "";
		if (prop == null) return dataType;
        dataType = prop.getRanges(ontologies).toString();
        return dataType;
    } // getRanges
    
    /**
     * Returns the local class name
     * @param cls
     * @return 
     */
    public String getLocalClassName(OWLClass cls) {
        return (cls.getIRI().toString().contains("#") ? cls.getIRI().toString().split("#")[1] : cls.getIRI().toString());
    }
    
    /**
     * Returns the local property name
     * @param prop
     * @return 
     */
    @SuppressWarnings("rawtypes")
	public String getLocalPropertyName(OWLProperty prop) {
        return (prop.getIRI().toString().contains("#") ? prop.getIRI().toString().split("#")[1] : prop.getIRI().toString());
    }
    
    /**
     * Returns the local class label
     * @param cls
     * @return String: label
     */
    public String getClassLabel(OWLClass cls) {
    	String value = null;
        if (cls == null) return value;
        OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        Set<OWLOntology> ontologies = ontology.getOWLOntologyManager().getOntologies();
        OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        Set<OWLAnnotation> ann = cls.getAnnotations(ontology, label);
        for(OWLAnnotation an : ann){
            value = an.getValue().toString().trim();
            break;
        } //for
        boolean set = false;
        if(value == null) {
            for(OWLOntology ont : ontologies)
            {
                if(ont.containsClassInSignature(cls.getIRI())){
                    Set<OWLAnnotation> ano = cls.getAnnotations(ont,label);
                    for(OWLAnnotation a : ano){
                        set = true;
                        value = a.getValue().toString();
                    } //for
                } //if
            } //for
            if(!set) value = cls.getIRI().getFragment();
        } //if
        if (value != null){
        if(value.contains("^^xsd:string")){
            value = value.replace("^^xsd:string","");
        }else{
        	if (value.contains("@en")){
            	value = value.replace("@en", "");
        	} //if
        }
        value = value.replace("\"", "");
        }
        return value;
    } // getClassLabel

    /**
     * Returns the Intersection part of OWLClassExpression
     * @param parent
     * @return OWLClassExpression: result
     */
    public OWLClassExpression seekNestedIntersection(OWLClassExpression parent) {
    	OWLClassExpression result = null;
    	for (OWLClassExpression childPart : parent.getNestedClassExpressions()){
    		if (childPart.getClassExpressionType().getName().equalsIgnoreCase("ObjectIntersectionOf")){
                result = childPart;
            }
        }
    	return result;
    }
    
    /**
     * Returns the local property label
     * @param OWLProperty: p
     * @return String: label
     */
    @SuppressWarnings("rawtypes")
	public String getPropertyLabel(OWLProperty p) {
        String rval = "";
        if (p == null) return rval;
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLAnnotationProperty label = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        for (OWLOntology o : ontologies) {
            for (OWLAnnotation annotation : p.getAnnotations(o, label)) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    rval = val.getLiteral();
                }
            }
        }
        return rval;
    } // getPropertyLabel
    
    /**
     * Returns the concept name of iri
     * @param String: iri
     * @return String: name
     */
    public String getConceptName(String iri) {
        if (iri.contains("#")) {
            return iri.split("#")[1];
        } else {
            String[] parts = iri.split("/");
            return parts[parts.length - 1];
        }
    }
    
    /**
     * Returns the definition of class
     * @param OWLClass: cls
     * @return  String: definition
     */
    public String getClassDefinition(OWLClass cls) {
    	if (cls == null) return null;
    	String ontTermDef = getDefinition(cls, ontology);
        if(ontTermDef == null){
        	Set<OWLOntology> ontologies = ontology.getOWLOntologyManager().getOntologies();
        	for(OWLOntology o : ontologies){
                if(ontTermDef == null){
                    ontTermDef = getDefinition(cls, o);
                }else{
                	break;
                }
            }
        }
        return ontTermDef;
    } // getClassDefinition
    
    /**
     * Returns the definition of class in a certain ontology
     * @param OWLClass: cls
     * @param OWLOntology: ont
     * @return  String: definition
     */
    private String getDefinition(OWLClass cls,OWLOntology ont){
    	if (cls == null) return null;
        String definition = null;
        RadiantToolConfig conf = new RadiantToolConfig();
        String defURI = conf.getDefinitionURI();
        OWLAnnotationProperty def = manager.getOWLDataFactory().getOWLAnnotationProperty(IRI.create(defURI));//ont.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty(IRI.create(defURI));
        OWLAnnotationProperty isdefby = manager.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_IS_DEFINED_BY.getIRI());//ont.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_IS_DEFINED_BY.getIRI());
        for (OWLAnnotation annotation : cls.getAnnotations(ont, isdefby)){
            if (annotation.getValue() instanceof OWLLiteral) {
            	OWLLiteral val = (OWLLiteral) annotation.getValue();
            	if (val.hasLang("en")) {
            		definition = val.getLiteral();
            	}
            }
        }
        if (definition == null){
        	Set<OWLAnnotation> annots = cls.getAnnotations(ont,def);
        	for(OWLAnnotation an : annots){
        		if(an.getValue() instanceof OWLLiteral){
                	OWLLiteral value = (OWLLiteral) an.getValue();
                	definition = value.getLiteral();
                }
            }
        }
        return definition;
    }
    
    /**
     * Returns the description of class
     * @param OWLClass: cls
     * @return  String: description
     */
    public String getOntologyConceptDescription(OWLClass cls){
    	String ontTermDesc = getDescription(cls, ontology);
    	if(ontTermDesc == null){
    		Set<OWLOntology> ontologies = ontology.getOWLOntologyManager().getOntologies();
    		for(OWLOntology o : ontologies){
        		if(ontTermDesc == null){
        			ontTermDesc = getDescription(cls, o);
        		}else{
        			break;
        		}
        	}
        }
        return ontTermDesc;
    }
    
    /**
     * Returns the description of class in a certain ontology
     * @param OWLClass: cls
     * @param OWLOntology: ont
     * @return  String: description
     */
	private String getDescription(OWLClass cls, OWLOntology ont) {
		String description = null;
        RadiantToolConfig conf = new RadiantToolConfig();
        String descURI = conf.getDescriptionURI();
        OWLAnnotationProperty desc = manager.getOWLDataFactory().getOWLAnnotationProperty(IRI.create(descURI));
        OWLAnnotationProperty rdfDesc = manager.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDF_DESCRIPTION.getIRI());
        for (OWLAnnotation annotation : cls.getAnnotations(ont, rdfDesc)){
            if (annotation.getValue() instanceof OWLLiteral){
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                if (val.hasLang("en")) {
                    description = val.getLiteral();
                }
            }
        }
        if (description == null){
            Set<OWLAnnotation> annots = cls.getAnnotations(ont,desc);
            for(OWLAnnotation an : annots){
                if(an.getValue() instanceof OWLLiteral){
                    OWLLiteral value = (OWLLiteral) an.getValue();
                    description = value.getLiteral();
                }
            }
        }
        return description;
	}

	/**
     * Returns the description of class in a certain ontology
     * @return  OWLClass: thing
     */
	public OWLClass getThing() {
		return thing;
	}
	
    /***********************************************************************************
     * Main method for testing.
     * 
     * @param args
     *            The command-line arguments.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // load owl file
        OntologyManager discoveryMgr = null;
        long start = System.currentTimeMillis();
        try {
            //discoveryMgr = new OntologyManager("camera.owl.rdf");
            //discoveryMgr = new OntologyManager("SUMO_Finance.owl");
            //discoveryMgr = new OntologyManager("pizza.owl");
            discoveryMgr = new OntologyManager("C:/Users/ucam10a/Desktop/XMLBox/OWLBox/webService.owl");
            //discoveryMgr = new OntologyManager("edam_v12.owl");
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));
        System.out.println();
        
        
        System.out.println("ontologies size = " + discoveryMgr.getOntologies().size());
        
        
        
        // get concept class by name
        start = System.currentTimeMillis();
        //OWLClass test = discoveryMgr.getConceptClass("http://www.xfront.com/owl/ontologies/camera/#Money");
        
        //** Piazza test
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#Food");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#InterestingPizza");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#American");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#SpicyPizza");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#SpicyPizzaEquivalent");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#FishTopping");
      
        //** obi test
        //OWLClass test = discoveryMgr.getConceptClass("http://purl.obolibrary.org/obo/webService.owl#Class_0013");
        //OWLClass test = discoveryMgr.getConceptClass("http://purl.obolibrary.org/obo/obi.owl#Class_6");
        //OWLClass test1 = discoveryMgr.getConceptClass("http://www.ifomis.org/bfo/1.1/snap#SpatialRegion");
        //OWLClass test = discoveryMgr.getConceptClass("http://purl.obolibrary.org/obo/IAO_0000005");
        OWLClass test = discoveryMgr.getThing();
        //OWLClass test = discoveryMgr.getConceptClass("http://purl.obolibrary.org/obo/OBI_0200036");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.ifomis.org/bfo/1.1/snap#GenericallyDependentContinuant");
        //OWLClass test = discoveryMgr.getConceptClass("http://purl.obolibrary.org/obo/IAO_0000400");
        
        // get by label
        //OWLClass test1 = discoveryMgr.getConceptClassByLabel("planned process");
        
        end = System.currentTimeMillis();
        System.out.println("test class = " + test);
        System.out.println("test class' label = " + discoveryMgr.getClassLabel(test));
        //System.out.println("test1 class = " + test1);
        //System.out.println("test1 class' label = " + discoveryMgr.getClassLabel(test1));
        System.out.println("latency = " + (end - start));
        System.out.println();

        // get direct super classes
        start = System.currentTimeMillis();
        Set<OWLClass> Directsupers = discoveryMgr.getDirectSuperClasses(test);
        end = System.currentTimeMillis();
        out.println("Direct supers class size = " + Directsupers.size());
        for (OWLClass cls : Directsupers) {
            System.out.println("   direct super cls = " + cls);
        }
        System.out.println("latency = " + (end - start));
        System.out.println();

        // get super class
        start = System.currentTimeMillis();
        ArrayList<Set<OWLClass>> Supers = discoveryMgr.getSuperClasses(test);
        end = System.currentTimeMillis();
        int num = 0;
        for (Set<OWLClass> clz : Supers) {
    		num = num + clz.size();
    	} // for
        out.println("Supers class size = " + num);
        for (Set<OWLClass> clz : Supers) {
    		for (OWLClass c : clz){
    			out.println("   super class = " + c);
    		} // for
    	} // for
        System.out.println("latency = " + (end - start));
        System.out.println();
        
        // get direct sub classes
        start = System.currentTimeMillis();
        Set<OWLClass> directSubs = discoveryMgr.getDirectSubClasses(test);
        end = System.currentTimeMillis();
        out.println("Direct sub class size = " + directSubs.size());
        for (OWLClass cls : directSubs) {
            System.out.println("   direct sub cls = " + cls);
        }
        System.out.println("latency = " + (end - start));
        System.out.println();

        // get sub class
        start = System.currentTimeMillis();
        ArrayList<Set<OWLClass>> Subs = discoveryMgr.getSubClasses(test);
        end = System.currentTimeMillis();
        num = 0;
        for (Set<OWLClass> clz : Subs) {
    		num = num + clz.size();
    	} // for
        out.println("Sub class size = " + num);
        for (Set<OWLClass> clz : Subs) {
    		for (OWLClass c : clz){
    			out.println("   Sub class = " + c);
    		} // for
    	} // for
        
        System.out.println("latency = " + (end - start));
        System.out.println();

        // get the equivalent classes
        start = System.currentTimeMillis();
        Set<OWLClass> equivs = discoveryMgr.getEquivalentClasses(test);
        end = System.currentTimeMillis();
        out.println("equivs class size = " + equivs.size());
        for (OWLClass cls : equivs) {
            System.out.println("   equiv cls = " + cls);
        }
        System.out.println("latency = " + (end - start));
        System.out.println();

        // getDataProperties of the class
        start = System.currentTimeMillis();
        Set<OWLDataProperty> dataProperties = discoveryMgr.getDataProperties(test);
        end = System.currentTimeMillis();
        out.println("data properties size = " + dataProperties.size());
        System.out.println("latency = " + (end - start));
        System.out.println();
        
        // Cardinality
        start = System.currentTimeMillis();
        for (OWLDataProperty dataProp : dataProperties) {
            System.out.println("   DataProperty = " + dataProp);
            System.out.println("   DataProperty label = " + discoveryMgr.getPropertyLabel(dataProp));
            // get the getCardinality of the data property and concept class
            out.println("      class cardinality = " + discoveryMgr.getCardinality(dataProp, test));
        }
        end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));
        System.out.println();
        
        
        // getObjectProperties of the class
        start = System.currentTimeMillis();
        Set<OWLObjectProperty> objProperties = discoveryMgr.getObjectProperties(test);
        end = System.currentTimeMillis();
        out.println("object properties size = " + objProperties.size());
        System.out.println("latency = " + (end - start));
        
        //start = System.currentTimeMillis();
        //Set<OWLObjectProperty> objProperties1 = discoveryMgr.getObjectProperties(test1);
        //end = System.currentTimeMillis();
        //out.println("object properties1 size = " + objProperties1.size());
        //System.out.println("latency = " + (end - start));
        
        // Cardinality
        start = System.currentTimeMillis();
        for (OWLObjectProperty objProp : objProperties) {
            System.out.println("   ObjectProperty = " + objProp);
            System.out.println("   ObjectProperty label = " + discoveryMgr.getPropertyLabel(objProp));
            // get the getCardinality of the object property and concept class
            out.println("      class cardinality = " + discoveryMgr.getCardinality(objProp, test));
        }
        end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));
        System.out.println();

        // getDomains of the class's object properties
        start = System.currentTimeMillis();
        for (OWLObjectProperty objProp : objProperties) {
            System.out.println("ObjectProperty = " + objProp);
            Set<OWLClass> domClazz = discoveryMgr.getDomains(objProp);
            for (OWLClass cls : domClazz) {
                System.out.println("   domain cls = " + cls);
            }
        }
        end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));
        System.out.println();

        // getRanges of the class's object properties
        start = System.currentTimeMillis();
        for (OWLObjectProperty objProp : objProperties) {
            System.out.println("ObjectProperty = " + objProp);
            Set<OWLClass> ranClazz = discoveryMgr.getRanges(objProp);
            for (OWLClass cls : ranClazz) {
                System.out.println("   range cls = " + cls);
            }
        }
        end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));
        System.out.println();

        
        // check the the class's object properties are functional or not
        for (OWLObjectProperty objProp : objProperties) {
            System.out.println("ObjectProperty = " + objProp);
            System.out.println("   is functional ? : "
                    + discoveryMgr.isFunctional(objProp));
        }
        System.out.println();

        // check the the class's object properties are inverse functional or not
        for (OWLObjectProperty objProp : objProperties) {
            System.out.println("ObjectProperty = " + objProp);
            System.out.println("   is inverse functional ? : "
                    + discoveryMgr.isInverseFunctional(objProp));
        }
        System.out.println();
        
        // print all class
        //discoveryMgr.printClasses();

    } // main
	

} // OntoLoader


