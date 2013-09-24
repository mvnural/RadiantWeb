/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uga.radiant.suggestion;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import edu.uga.radiant.util.RadiantToolConfig;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 *
 * @author Chaitu
 */
public class GetOntologyConcepts {

    private static RadiantToolConfig conf = new RadiantToolConfig();

    private static OWLOntologyManager manager;
    
    /** Given Ontology gets all the subclasses of an operation URI
     * @param ont OWLOntology
     * @returns the descendents of Operation URI(i.e uri of concept related to Web service operation) in the Ontology
     *
     * */
    public static Set<OWLClass> getOperationConcetps(OWLOntology ont){

        String opConceptURI = conf.getOperationConcept();
        OWLDataFactory owlDataFactory = ont.getOWLOntologyManager().getOWLDataFactory();
        OWLClass opConcept = owlDataFactory.getOWLClass(IRI.create(opConceptURI));
        Set<OWLClass> conceptsList = getSubClass(opConcept,ont);//new ArrayList<OWLClass>();
        return conceptsList;

    }
    
    /** Given Ontology gets all the subclasses of an operation URI
     * @param ont OWLOntology
     * @param opiri Ontology Concept IRI
     * @returns the descendents of Operation URI(i.e uri of concept related to Web service operation) in the Ontology
     *
     * */
    public static Set<OWLClass> getOperationConcetps(OWLOntology ont, String opiri){

        OWLDataFactory owlDataFactory = ont.getOWLOntologyManager().getOWLDataFactory();
        OWLClass opConcept = owlDataFactory.getOWLClass(IRI.create(opiri));
        Set<OWLClass> conceptsList = getSubClass(opConcept,ont);//new ArrayList<OWLClass>();
        return conceptsList;

    }

    /** Given Ontology gets all the subclasses of an operation URI
     * @param ont OWLOntology
     * @returns the descendents of Operation URI(i.e uri of concept related to Web service operation) in the Ontology
     *
     * */
    public static Set<OWLClass> getParameterConcetps(OWLOntology ont, String paramIRI){

        //String paramConceptURI = conf.getParameterConcept();
        OWLDataFactory owlDataFactory = ont.getOWLOntologyManager().getOWLDataFactory();
        OWLClass opConcept = owlDataFactory.getOWLClass(IRI.create(paramIRI));
        Set<OWLClass> conceptsList = getSubClass(opConcept,ont);//new ArrayList<OWLClass>();
        return conceptsList;

    }

    /**
     * Given OWLClass and OWLOntology gives the set all descendents of that class
     * @param cls OWLClass
     * @param ont OWLOntology
     * @returns the Set of descendent classes of cls
     * */
    public static Set<OWLClass> getSubClass(OWLClass cls,OWLOntology ont){

        Set<OWLClass> subCls = new HashSet<OWLClass>();
        List<OWLClass> localCopy = new ArrayList<OWLClass>();
        Set<OWLOntology> ontologies = ont.getOWLOntologyManager().getOntologies();
        for(OWLOntology o : ontologies)
        {
            Set<OWLClassExpression> set = cls.getSubClasses(o);
            for(OWLClassExpression oce : set){
                if(oce != null)
                {
                    OWLClass cls1 = oce.asOWLClass();
                    Set<OWLClass> list = getSubClass(cls1,o);
                    subCls.add(cls1);
                    subCls.addAll(list);
                    localCopy.add(cls1);
                }
            }
        }
        return subCls;
    }

    /**
     * Given an OWLClass and the Ontology gets the definition of the OWLClass
     * @param OWLClass cls
     * @param OWLOntology ont
     * @return the definition of class
     * */
    public String getDefinition(OWLClass cls,OWLOntology ont){
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
     * Gets the label for the particular owlclass cls in a given OWLOntology onto
     * @param cls OWLClass
     * @param onto OWLOntology
     * @return label for the OWLClass
     */
    public String getLabel(OWLClass cls,OWLOntology onto)
    {
        OWLDataFactory factory = onto.getOWLOntologyManager().getOWLDataFactory();
        Set<OWLOntology> ontologies = onto.getOWLOntologyManager().getOntologies();
        String value = null;
        OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        Set<OWLAnnotation> ann = cls.getAnnotations(onto, label);
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
                    for(OWLAnnotation a:ano)
                    {
                        set = true;
                        value = a.getValue().toString();//getAnnotationValue().toString();
                    } //for

                } //if

            } //for
            if(!set)
                value = cls.getIRI().getFragment();//toStringID();
        } //if
        if(value.contains("^^xsd:string")){
            value = value.replace("^^xsd:string", "");
        } //if
        else
        if (value.contains("@en")){
            value = value.replace("@en", "");
        } //if
        value = value.replace("\"", "");
        return value;
        
    } //getLabel

    public static void main(String [] args) throws OWLOntologyCreationException{

        
    }
    
}
