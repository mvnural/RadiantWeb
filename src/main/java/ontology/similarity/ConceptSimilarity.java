/**
 * Concept Similarity 
 */
package ontology.similarity;

//import StringMatcher.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.semanticweb.owlapi.model.*;

import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.stringmetrics.CompareDefinition;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

/**
 * @author Rui Wang, Michael Cotterell
 * 
 */
public class ConceptSimilarity {

    /** Given a concept IRI, return the OWLClass
     * @param conceptIRI     full URI (not only name) of a concept
     * @return  the OWLClass object 
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    public static OWLClass getConceptClass(String conceptIRI, String owlURI) throws OWLOntologyCreationException, IOException 
    {
        OWLClass conceptClass = null;
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        conceptClass = parser.getConceptClass(conceptIRI);
        return conceptClass;
    } // getConceptClass

    /** Given ontology file name, check if the output annotated with 
     *  outConceptIRI is safe to be fed into input annotated with inConceptIRI.
     *  safe: same, equivalent or input is ancestor of output
     * @param inConceptIRI
     * @param outConceptIRI
     * @param owlURI
     * @return 0 not safe, 1 safe
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    public static int conceptMapSafe(String inConceptIRI, String outConceptIRI, String owlURI) throws OWLOntologyCreationException, IOException 
    {
        OntologyManager parser = OntologyManager.getInstance(owlURI);

        int safe = 0;
        
        // not given concept
        if (inConceptIRI == null || outConceptIRI == null || !inConceptIRI.contains("http://") || !outConceptIRI.contains("http://")) {
            return 0;
        } // if

        OWLClass inConceptClass = getConceptClass(inConceptIRI, owlURI);
        OWLClass outConceptClass = getConceptClass(inConceptIRI, owlURI);

        if (inConceptClass == null || outConceptClass == null) {
            return 0;
        } // if

        // semantically safe: same, equivallent, or input is ancestor of output
        if (parser.hasSuperClass(outConceptClass, inConceptClass) || inConceptClass.getIRI().equals(outConceptClass.getIRI()) || parser.hasEquivalentClass(inConceptClass, outConceptClass)) {
            safe = 1;
        } // if
        
        return safe;
        
    } // conceptMapSafe

    /** Computes the syntactic similarity between two concepts in the ontology.
     * @param Pst
     * @param Pcs
     * @return
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    public static double synSim (OWLClass Pst, OWLClass Pcs, String owlURI) throws URISyntaxException, OWLOntologyCreationException, IOException 
    {
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        double wName = 0.2;
        double wLabel = 0.8;

        QGramsDistance mc = new QGramsDistance();
        
        String PstLocalName = parser.getConceptName(Pst.getIRI().toString());
        String PcsLocalName = parser.getConceptName(Pcs.getIRI().toString());
        
        String PstLabel = parser.getClassLabel(Pst);
        String PcsLabel = parser.getClassLabel(Pcs);
        
        String PstDefinition = parser.getClassDefinition(Pst);
        String PcsDefinition = parser.getClassDefinition(Pcs);
        
        double scoreName = mc.getSimilarity(PstLocalName, PcsLocalName);
        double scoreLabel = mc.getSimilarity(PstLabel, PcsLabel);
        
        // Comparing definitions.
        double scoreDef = CompareDefinition.getSimilarity(PstDefinition, PcsDefinition);
        double scoreTerm = (wName * scoreName) + (wLabel * scoreLabel);
        
        double score = ( scoreDef * 0.8 ) + ( scoreTerm * 0.2);
        return score;
        
    } // synSim
    
    /*
     * @see
     * ontology.interfaces.SimilarityFactory#getConceptSimScore(java.lang.String
     * , java.lang.String)
     */
    /*
     * @see
     * ontology.interfaces.SimilarityFactory#getConceptSimScore(java.lang.String
     * , java.lang.String)
     */
    public static double getConceptSimScore(OWLClass inConceptClass, OWLClass outConceptClass, String owlURI) throws URISyntaxException, OWLOntologyCreationException, IOException 
    {
    	
    	double weightSyn  = 0.2;
        double weightProp = 0.4; 
        double weightCvrg = 0.4;
            
        // syntactic similarity
        double synScore = ConceptSimilarity.synSim(inConceptClass, outConceptClass, owlURI);
        
        // if either of the annotations is not class, return only 
        // weight*syntactic score of comparing two concept names
        if (inConceptClass == null || outConceptClass == null) {
            return weightSyn * synScore;
        } // if
        
        // property similarity
        double propScore = PropertySimilarity.getPropertySimScore(inConceptClass, outConceptClass, owlURI);

        // coverage similarity
        double cvrgScore = CoverageSimilarity.getCvrgSimScore(inConceptClass, outConceptClass, owlURI);
        
        // weighted sum
        double score = (weightSyn * synScore) + (weightProp * propScore) + (weightCvrg * cvrgScore);
        
        return score;
        
    } // getConceptSimScore
    

    /** For testing
     * @param args
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    public static void main(String[] args) throws URISyntaxException, OWLOntologyCreationException, IOException 
    {
        String owlURI = "owl/obi.owl";
        
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        String concept1 = "http://purl.obolibrary.org/obo/webService.owl#Class_0013";
        String concept2 = "http://purl.obolibrary.org/obo/obi.owl#Class_34";
        
        OWLClass cls1 = parser.getConceptClass(concept1);
        OWLClass cls2 = parser.getConceptClass(concept2);
        
        double score = ConceptSimilarity.getConceptSimScore(cls1, cls2, owlURI);
        
        System.out.println("overall concept similarity score = " + score);
        
    } // main
}
