package edu.uga.radiant.ajax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.wstool.parser.sawsdl.ComplexTypeOBJ;
import edu.uga.cs.wstool.parser.sawsdl.MessageOBJ;
import edu.uga.cs.wstool.parser.sawsdl.OperationOBJ;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.sawsdl.SimpleTypeOBJ;
import edu.uga.cs.wstool.parser.xml.XMLParser;
import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.suggestion.LuceneIndex;
import edu.uga.radiant.util.RadiantToolConfig;

public class RecommendAll extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** response error message
     */
	private String errormsg;
	
	/** WSDL element identifier of SAWSDLParser
     */
	private ArrayList<String> eleIDs;
	
	/** WSDL element name
     */
	private ArrayList<String> eleNames;
	
	/** WSDL element type (ex. operation, simple type)
     */
	private ArrayList<String> eleTypes;
	
	/** WSDL element attribute (ex. modelReference, schema mapping)
     */
	private ArrayList<String> attributes;
	
	/** WSDL element IRI
     */
	private ArrayList<String> IRIs;
	
	/** concept labels
     */
	private ArrayList<String> labels;
	
	/** the tab index of owl
     */
	private String tabIndex;
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
	    Logger logger = RadiantToolConfig.getLogger();
		OntologyManager mgr = null;
		ArrayList<String> operationIRI = new ArrayList<String>();
		ArrayList<String> paramIRI = new ArrayList<String>();
		eleIDs = new ArrayList<String>();
		eleNames = new ArrayList<String>();
		eleTypes = new ArrayList<String>();
		attributes = new ArrayList<String>();
		IRIs = new ArrayList<String>();
		labels = new ArrayList<String>();
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		
		if (tabIndex == null){
			errormsg = "Error:ontology not loaded";
			return ERROR;
		}
		
		if (tabIndex.equals("0")){
			mgr = (OntologyManager)session.get("OntologyManager");
		}else{
			mgr = (OntologyManager)session.get("OntologyManager" + tabIndex);
		}
		operationIRI = (ArrayList<String>)session.get("operation");
		paramIRI = (ArrayList<String>)session.get("parameter");
		errormsg = "";
		
		XMLParser wsparser = (SAWSDLParser)session.get("wsdlparser");
		
		try {
			
			if(mgr != null){
				
				// create lucene
				LuceneIndex lucene = new LuceneIndex(mgr);
				
				// get all simple types
				HashSet<SimpleTypeOBJ> simples = new HashSet<SimpleTypeOBJ>();
				
				for (OperationOBJ op : ((SAWSDLParser) wsparser).getAllOperations().values()){
					
					MessageOBJ in = op.getInput();
					simples.addAll(in.getSimpleType());
					for (ComplexTypeOBJ cplex : in.getComplexType()){
						simples.addAll(SAWSDLParser.getAllSimpleType(cplex));
					}

					MessageOBJ out = op.getOutput();
					simples.addAll(out.getSimpleType());
					for (ComplexTypeOBJ cplex : out.getComplexType()){
						simples.addAll(SAWSDLParser.getAllSimpleType(cplex));
					}
					
					// get recommend operation concept from lucene 
					String iri = lucene.getTopOperationMatchClass(op.getDoc());
					if (iri == null) iri = "";
					String label = mgr.getClassLabel(mgr.getConceptClass(iri));
					
					eleIDs.add(op.getId()+ "");
					eleNames.add(op.getName());
					eleTypes.add("operation");
					attributes.add("modelReference");
					IRIs.add(iri);
					labels.add(label);
					
				}
				
				if ((operationIRI == null) || (paramIRI == null)){
					operationIRI = LoadOWL.getOwlOperationSuperClasses(mgr);
					session.put("operation", operationIRI);
					paramIRI = LoadOWL.getOwlParamSuperClasses(mgr);
					session.put("parameter", paramIRI);
				}
				
				int counter = 0;
				for (SimpleTypeOBJ simple : simples){
					// get recommend simple type concept from Lucene
					String iri = lucene.getTopParamMatchClass(simple.getDescription());
					if (iri == null) {
						iri = "";
					}else{
						counter += 1;
					}
					String label = mgr.getClassLabel(mgr.getConceptClass(iri));
					
					logger.debug("simple name = " + simple.getName() + ", ");
					logger.debug("iri = " + iri);
					
					eleIDs.add(simple.getId() + "");
					eleNames.add(simple.getName());
					eleTypes.add("simple");
					attributes.add("modelReference");
					IRIs.add(iri);
					labels.add(label);
				}
				
				logger.debug("counter = " + counter);
				
				if (counter == 0) {
					errormsg = "No suggestion available";
					return ERROR;
				}
				
			}else{
	        	errormsg = "Error:ontology not loaded";
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			errormsg = e.toString();
			// record error log
			logger.error(e.toString());
		}
		
		return SUCCESS;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setEleIDs(ArrayList<String> eleIDs) {
		this.eleIDs = eleIDs;
	}

	public ArrayList<String> getEleIDs() {
		return eleIDs;
	}

	public void setEleNames(ArrayList<String> eleNames) {
		this.eleNames = eleNames;
	}

	public ArrayList<String> getEleNames() {
		return eleNames;
	}

	public void setEleTypes(ArrayList<String> eleTypes) {
		this.eleTypes = eleTypes;
	}

	public ArrayList<String> getEleTypes() {
		return eleTypes;
	}

	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}

	public ArrayList<String> getAttributes() {
		return attributes;
	}

	public void setIRIs(ArrayList<String> iRIs) {
		IRIs = iRIs;
	}

	public ArrayList<String> getIRIs() {
		return IRIs;
	}

	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getTabIndex() {
		return tabIndex;
	}

	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}

	public ArrayList<String> getLabels() {
		return labels;
	}
	
	
}