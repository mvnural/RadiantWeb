package edu.uga.radiant.suggestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import edu.uga.cs.wstool.parser.sawsdl.ComplexTypeOBJ;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.sawsdl.SimpleTypeOBJ;
import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.stringmetrics.CompareDefinition;
import edu.uga.radiant.stringmetrics.CompareTerm;
import edu.uga.radiant.stringmetrics.CompareTermDef;
import edu.uga.radiant.util.SortValueMap;


public class Test1 {
	
	/**
     * for test
     */
    public static void main(String[] args) throws Exception {
    	
    	String indent = "";
    	int rank = 1;
    	OntologyManager mgr = null;
    	String owlURI = "file:/D:/test1.owl";
        try {
            mgr = OntologyManager.getInstance(owlURI);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        
        ComplexTypeOBJ cplex = new ComplexTypeOBJ();
        cplex.setName("account");
        cplex.setDescription("An account held by an investor at a financial institution. The financial institution holds the money for the investor, leading to a positive or credit balance, or loans money to the investor, leading to a negative or debit balance. Unlike a brokerage account, which allows an investor to buy and sell securities, a bank account is used for savings. Types of bank accounts include savings accounts and checking accounts.");
        
        List<SimpleTypeOBJ> leaves = new ArrayList<SimpleTypeOBJ>();
        SimpleTypeOBJ simpleobj = new SimpleTypeOBJ();
        OWLClass test1 = mgr.getConceptClass("http://www.semanticweb.org/ontologies/2012/5/Ontology1338687906343.owl#accountNumber");
        simpleobj.setName("accountNumber");
        simpleobj.setModelReference(test1.getIRI());
        leaves.add(simpleobj);
        
        simpleobj = new SimpleTypeOBJ();
        OWLClass test2 = mgr.getConceptClass("http://www.semanticweb.org/ontologies/2012/5/Ontology1338687906343.owl#balance");
        simpleobj.setName("balance");
        simpleobj.setModelReference(test2.getIRI());
        leaves.add(simpleobj);
        cplex.setSimples(leaves);
        
        // print complex type
        SAWSDLParser.printComplexType(indent, cplex);
		System.out.println("start process-------------------------------------");
		
		// transform data
        Set<Node> leafNodes = SimpleStringMatcher.dataTransform(cplex, mgr);
        
        // get candidates
        Set<SuggestionOBJ> candidates = SimpleStringMatcher.getSchemaMappingSuggestion(leafNodes, mgr);
		
        // sort score
        SortValueMap<OWLClass, Double> score = new SortValueMap<OWLClass, Double>();
		for (SuggestionOBJ obj : candidates){
			OWLClass cls = mgr.getConceptClass(obj.getConceptIRI());
			double labelScore = CompareTerm.getSimilarity(cplex.getName(), mgr.getClassLabel(cls));//qgd.getSimilarity(wsdlEleNa`me,clName);
            double descScore = CompareDefinition.getSimilarity(cplex.getDescription(), mgr.getClassDefinition(cls));//qgd.getSimilarity(wsdlEleDoc, clsDesc);
            double labeldescScore = CompareTermDef.getSimilarity(cplex.getName(), mgr.getClassDefinition(cls));
            double labeldescScore1 = CompareTermDef.getSimilarity(mgr.getClassLabel(cls), cplex.getDescription());
            labeldescScore = (labeldescScore + labeldescScore1)/2;
            double newScore = SimpleStringMatcher.getTotalScore(labelScore, descScore, labeldescScore);
            
            
            System.out.println("candidate class = " + obj.getConceptIRI());
            System.out.println("def = " + mgr.getClassDefinition(cls));
            System.out.println("syntatic score = " + newScore);
            System.out.println("schema score = " + obj.getScore());
            
            
            Double totalScore = Math.sqrt(newScore * obj.getScore());
            score.put(cls, totalScore);
		}
		System.out.println("===========================================================");
		System.out.println("<Suggestion is following>");
		rank = 1;
		for (OWLClass cls : score.keySet()){
			System.out.println(rank + ". class = " + mgr.getClassLabel(cls) + ", score = " + score.get(cls));
			rank++;
		}
		OWLClass topCls = (OWLClass) score.keySet().iterator().next();
		cplex.setModelReference(topCls.getIRI());
        
		
		System.out.println("\n");
        
		
		
		ComplexTypeOBJ cplex1 = new ComplexTypeOBJ();
        cplex1.setName("person");
        cplex1.setDescription("Human being who has the a name and a living address. He can apply a account in a bank to store or withdraw money.");
        
        leaves = new ArrayList<SimpleTypeOBJ>();
        ArrayList<ComplexTypeOBJ> Nodes = new ArrayList<ComplexTypeOBJ>();
        simpleobj = new SimpleTypeOBJ();
        test1 = mgr.getConceptClass("http://www.semanticweb.org/ontologies/2012/5/Ontology1338687906343.owl#name");
        simpleobj.setName("name");
        simpleobj.setModelReference(test1.getIRI());
        leaves.add(simpleobj);
        
        simpleobj = new SimpleTypeOBJ();
        test2 = mgr.getConceptClass("http://www.semanticweb.org/ontologies/2012/5/Ontology1338687906343.owl#address");
        simpleobj.setName("address");
        simpleobj.setModelReference(test2.getIRI());
        leaves.add(simpleobj);
        
        // set the previous complex type
        Nodes.add(cplex);
        
        // set the complex type person
        cplex1.setSimples(leaves);
        cplex1.setComplextypes(Nodes);
        
        // print complex type
        SAWSDLParser.printComplexType(indent, cplex1);
        System.out.println("start process-------------------------------------");
		
        // transform data structure
        leafNodes = SimpleStringMatcher.dataTransform(cplex1, mgr);
        
        // get candidates
        candidates = SimpleStringMatcher.getSchemaMappingSuggestion(leafNodes, mgr);
		
        // sort score 
        score = new SortValueMap<OWLClass, Double>();
		for (SuggestionOBJ obj : candidates){
			OWLClass cls = mgr.getConceptClass(obj.getConceptIRI());
			double labelScore = CompareTerm.getSimilarity(cplex1.getName(), mgr.getClassLabel(cls));//qgd.getSimilarity(wsdlEleNa`me,clName);
            double descScore = CompareDefinition.getSimilarity(cplex1.getDescription(), mgr.getClassDefinition(cls));//qgd.getSimilarity(wsdlEleDoc, clsDesc);
            double labeldescScore = CompareTermDef.getSimilarity(cplex1.getName(), mgr.getClassDefinition(cls));
            double labeldescScore1 = CompareTermDef.getSimilarity(mgr.getClassLabel(cls), cplex1.getDescription());
            labeldescScore = (labeldescScore + labeldescScore1)/2;
            double newScore = SimpleStringMatcher.getTotalScore(labelScore, descScore, labeldescScore);
            
            
            System.out.println("candidate class = " + obj.getConceptIRI());
            System.out.println("def = " + mgr.getClassDefinition(cls));
            System.out.println("syntatic score = " + newScore);
            System.out.println("schema score = " + obj.getScore());
            
            
            
            Double totalScore = Math.sqrt(newScore * obj.getScore());
            score.put(cls, totalScore);
		}
		System.out.println("===========================================================");
		System.out.println("<Suggestion is following>");
		rank = 1;
		for (OWLClass cls : score.keySet()){
			System.out.println(rank + ". class = " + mgr.getClassLabel(cls) + ", score = " + score.get(cls));
			rank++;
		}
        
        
        
        /*
        // access by file system
    	URL fileURL = new URL("file:/D:/clustalw2.sawsdl");
    	SAWSDLParser semP = new SAWSDLParser(fileURL);
		
    	for (OperationOBJ oper : semP.getAllOperations().values()){
        	if (oper.getName().equals("run")){
        		System.out.println("operation name = " + oper.getName());
    			Set<OWLClass> leaves = new HashSet<OWLClass>();
    			ComplexTypeOBJ cplex = oper.getInput().getComplextype().get(0);
        		for (SimpleTypeOBJ simple : SAWSDLParser.getAllSimpleType(cplex)){
    				OWLClass cls = mgr.getConceptClass(simple.getModelReference().toString());
    				if (cls != null) leaves.add(cls);
        		}
        		Set<SuggestionOBJ> candidates = getRestrictCollection(leaves, mgr, SAWSDLParser.getAllSimpleType(cplex).size());
        		SortValueMap<OWLClass, Double> score = new SortValueMap<OWLClass, Double>();
        		for (SuggestionOBJ obj : candidates){
        			OWLClass cls = mgr.getConceptClass(obj.getConceptIRI());
        			double labelScore = CompareTerm.getSimilarity(cplex.getName(), mgr.getClassLabel(cls));//qgd.getSimilarity(wsdlEleNa`me,clName);
                    double descScore = CompareDefinition.getSimilarity(cplex.getDescription(), mgr.getClassDefinition(cls));//qgd.getSimilarity(wsdlEleDoc, clsDesc);
                    double labeldescScore = CompareTermDef.getSimilarity(cplex.getName(), mgr.getClassDefinition(cls));
                    double labeldescScore1 = CompareTermDef.getSimilarity(mgr.getClassLabel(cls), cplex.getDescription());
                    labeldescScore = (labeldescScore + labeldescScore1)/2;
                    double descWeight = 0;
                    double labelWeight = 0;
                    double labeldescWeight = 0;
                    if(descScore>0.4){
                        descWeight = 0.8;
                        labelWeight = 0.1;
                        labeldescWeight = 0.1;
                    }
                    else{
                        descWeight = 0.6;
                        labelWeight = 0.2;
                        labeldescWeight = 0.2;
                    }
                    Double newScore = new Double((descWeight*descScore + labelWeight*labelScore + labeldescScore*labeldescWeight) / (descWeight + labelWeight + labeldescWeight));
                    
                    Double totalScore = Math.sqrt(newScore * obj.getScore());
                    score.put(cls, totalScore);
        		}
        		System.out.println("complex type name = " + cplex.getName() + ", suggestion is ");
        		for (OWLClass cls : score.keySet()){
        			System.out.println("class = " + mgr.getClassLabel(cls) + ", score = " + score.get(cls));
        		}
        	} // if
        }
        */
    }
}
