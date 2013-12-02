package edu.uga.cs.lumina.discovery.action;

import static java.util.Arrays.deepToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.lumina.discovery.util.DiscoveryManager;
import edu.uga.cs.lumina.discovery.util.ErrorMessage;
import edu.uga.cs.lumina.discovery.util.OperationInfo;
import edu.uga.cs.lumina.discovery.util.WsOpReader;
import edu.uga.cs.wstool.parser.sawsdl.ComplexTypeOBJ;
import edu.uga.cs.wstool.parser.sawsdl.MessageOBJ;
import edu.uga.cs.wstool.parser.sawsdl.SimpleTypeOBJ;
import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.ontology.Triple;
import edu.uga.radiant.util.RadiantToolConfig;
import edu.uga.radiant.util.SortValueMap;

/**
 * @author Long
 * 
 * This is an Struts2 action java bean, which is responsible for the discovery 
 *
 */
public class Discovery extends ActionSupport {
	
	/***********************************************************************************
     * Get the the inputs and outputs from the website.
     * 
     * @return String: if success return "success", otherwise return "error".
     */
	private static final long serialVersionUID = 1L;
	
	/**
	 * debug model
	 */
	private static boolean DEBUG = false;
	
	/**
	 * the input html tag value which mean the owl file location
	 */
	private String owlloc;
	
	/**
	 * the multiple input html tag value which mean the operation name
	 */
	private String[] operationName;	// for multiple operations in the future
	
	/**
	 * the multiple input html tag value which mean the operation functionality semantic concept iri
	 */
	private String[] operationConcept;	// for multiple operations in the future
	
	/**
	 * the multiple input html tag value which mean the operation functionality description
	 */
	private String[] operationText;	// for multiple operations in the future
	
	/**
	 * the multiple input html tag value which mean the input name
	 */
	private String[] inputName;
	
	/**
	 * the multiple input html tag value which mean the input type, ex. xsd:string
	 */
	private String[] inputType;
	
	/**
	 * the multiple input html tag value which mean the input semantic concept iri
	 */
	private String[] inputConcept;
	
	/**
	 * the multiple input html tag value which mean the input description
	 */
	private String[] inputText;
	
	/**
	 * the multiple input html tag value which mean the output semantic concept iri
	 */
	private String[] outputName;
	
	/**
	 * the multiple input html tag value which mean the output type, ex. xsd:string
	 */
	private String[] outputType;
	
	/**
	 * the multiple input html tag value which mean the output semantic concept iri
	 */
	private String[] outputConcept;
	
	/**
	 * the multiple input html tag value which mean the output description
	 */
	private String[] outputText;
	
	/**
	 * the input html tag value which mean the possible service name
	 */
	private String serviceName;
	
	/**
	 * the input html tag value which mean the searching service description
	 */
	private String serviceDoc;
	
	/**
	 * the out print vector which collect the error message
	 */
	private Vector<ErrorMessage> vecError;
	
	/**
	 * the out print value which mean the error message
	 */
	private ErrorMessage errorMesg;
	
	/**
	 * the outcome operation information which show the sawasdl file result
	 */
	private ArrayList<OperationInfo> showSawsdlResult;
	
	/**
	 * the outcome operation information which show the wsdl file result
	 */
	private ArrayList<OperationInfo> showWsdlResult;
	
	/**
	 * the String Set which define the possible property name which may used to identify the complex type
	 */
	private static Set<String> has_part_pattern = new HashSet<String>() {
		private static final long serialVersionUID = 1L;
	{
	    add("has"); add("have"); add("part"); add("include"); add("contain"); add("comprise");
	}};

	
	/**
	 * Struts2 action
	 */
	public String execute() {
		
		// get contextPath ex.Radiant
		HttpServletRequest req = ServletActionContext.getRequest();
		String scheme = req.getScheme();             // http
	    String serverName = req.getServerName();     // hostname.com
	    int serverPort = req.getServerPort();        // 80
	    String contextPath = req.getContextPath();   // /mywebapp
	    String urlbase = "";
	    if (serverPort != 80){
	    	urlbase = scheme + "://" + serverName + ":" + serverPort + contextPath + "/";
		}else{
			urlbase = scheme + "://" + serverName + contextPath + "/";
		}
		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		OntologyManager mgr = (OntologyManager)session.get("OntologyManager");
		if (owlloc == null) owlloc = "";
		if (operationName == null) operationName = new String[] {""};
		if (operationConcept == null) operationConcept = new String[] {""};
		if (operationText == null) operationText = new String[] {""};
		if (inputName == null) inputName = new String[] {""};
		if (inputType == null) inputType = new String[] {""};
		if (inputConcept == null) inputConcept = new String[] {""};
		if (inputText == null) inputText = new String[] {""};
		if (outputName == null) outputName = new String[] {""};
		if (outputType == null) outputType = new String[] {""};
		if (outputConcept == null) outputConcept = new String[] {""};
		if (outputText == null) outputText = new String[] {""};
		if (serviceDoc == null) serviceDoc = "";
		if (serviceName == null) serviceName = "";
		
		try{
			
			int start = 0;
			int end = Discovery.class.getResource("").toString().indexOf(contextPath);
			String baseURI = Discovery.class.getResource("").toString().substring(start, end) + contextPath + "/";
			
			// Get the results, the first HashMap's key is the service name and the inside HashMap is operation's score
			HashMap<String, HashMap<String, Double>> SAWSDLResult = new HashMap<String, HashMap<String, Double>>();
			HashMap<String, HashMap<String, Double>> WSDLResult = new HashMap<String, HashMap<String, Double>>();
			
			// Catch the error message then return to the website
			vecError = new Vector<ErrorMessage>();
			
			// get the request operation number, it should be "1" now
			int operationNum = 1;
			// get the inputs number
			int[] operationInputNum = new int[operationNum];
			for (int i = 0; i < operationInputNum.length; i++) operationInputNum[i] = inputName.length;
			// get the outputs number
			int[] operationOutputNum = new int[operationNum];
			for (int i = 0; i < operationOutputNum.length; i++) operationOutputNum[i] = outputName.length;
			
			
			// define all operations, inputs and outputs, use ArrayList to put multiple operations for future, right now just one operation
			ArrayList<ArrayList<String>> operationInputs = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> operationInputType = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> operationInputText = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> operationInputConcept = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> operationOutputs = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> operationOutputType = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> operationOutputText = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> operationOutputConcept = new ArrayList<ArrayList<String>>();

			// for multiple operations in the future, but it has only one operation now
			if (DEBUG) System.out.println("operationName = " + operationName[0]);
			if (DEBUG) System.out.println("operationText = " + operationText[0]);
			if (DEBUG) System.out.println("operationConcept = " + operationConcept[0]);
			
			int inputCount = 0;
			int inputTypeCount = 0;
			int inputConceptCount = 0;
			int inputTextCount = 0;
			int outputCount = 0;
			int outputTypeCount = 0;
			int outputConceptCount = 0;
			int outputTextCount = 0;
			for (int i = 0; i < operationNum; i++){
				ArrayList<String> temp = new ArrayList<String>();
				for (int j = 0; j < operationInputNum[i]; j++){
					temp.add(inputName[inputCount]);
					if (DEBUG) System.out.println("operationInputs[" + i + "][" + j + "] = " + inputName[inputCount]);
					inputCount++;
				}
				operationInputs.add(temp);
				
				temp = new ArrayList<String>();
				for (int j = 0; j < operationInputNum[i]; j++){
					temp.add(inputType[inputTypeCount]);
					if (DEBUG) System.out.println("operationInputType[" + i + "][" + j + "] = " + inputType[inputTypeCount]);
					inputTypeCount++;
				}
				operationInputType.add(temp);
				
				temp = new ArrayList<String>();
				for (int j = 0; j < operationInputNum[i]; j++){
					temp.add(inputText[inputConceptCount]);
					if (DEBUG) System.out.println("operationInputText[" + i + "][" + j + "] = " + inputConcept[inputConceptCount]);
					inputTextCount++;
				}
				operationInputText.add(temp);
				
				temp = new ArrayList<String>();
				for (int j = 0; j < operationInputNum[i]; j++){
					temp.add(inputConcept[inputConceptCount]);
					if (DEBUG) System.out.println("operationInputConcept[" + i + "][" + j + "] = " + inputConcept[inputConceptCount]);
					inputConceptCount++;
				}
				operationInputConcept.add(temp);
				
				temp = new ArrayList<String>();
				for (int j = 0; j < operationOutputNum[i]; j++){
					temp.add(outputName[outputCount]);
					if (DEBUG) System.out.println("operationOutputs[" + i + "][" + j + "] = " + outputName[outputCount]);
					outputCount++;
				}
				operationOutputs.add(temp);
				
				temp = new ArrayList<String>();
				for (int j = 0; j < operationOutputNum[i]; j++){
					temp.add(outputType[outputTypeCount]);
					if (DEBUG) System.out.println("operationOutputType[" + i + "][" + j + "] = " + outputType[outputTypeCount]);
					outputTypeCount++;
				}
				operationOutputType.add(temp);
				
				temp = new ArrayList<String>();
				for (int j = 0; j < operationOutputNum[i]; j++){
					temp.add(outputText[outputConceptCount]);
					if (DEBUG) System.out.println("operationOutputText[" + i + "][" + j + "] = " + outputConcept[outputConceptCount]);
					outputTextCount++;
				}
				operationOutputText.add(temp);
				
				temp = new ArrayList<String>();
				for (int j = 0; j < operationOutputNum[i]; j++){
					temp.add(outputConcept[outputConceptCount]);
					if (DEBUG) System.out.println("operationOutputConcept[" + i + "][" + j + "] = " + outputConcept[outputConceptCount]);
					outputConceptCount++;
				}
				operationOutputConcept.add(temp);
			}
			
			OntologyManager discoveryMgr = null;
			String owlIRI = "";
			if (owlloc.contains("http://")){
				owlIRI = owlloc;
			}else if (!owlloc.equals("")){
				owlIRI = baseURI + "XMLBox/OWLBox/" + owlloc;
			}else{
			    errorMesg = new ErrorMessage();
                errorMesg.setErrormessage("Please import owl file first");
                vecError.add(errorMesg);
                return ERROR;
			}
			if (mgr != null){
				discoveryMgr = mgr;
			}else{
				try{
					// get ontology
					if (DEBUG) System.out.println("Accessing the " + owlIRI + " ontology\n");
					// create discovery manager
					discoveryMgr = OntologyManager.getInstance(owlIRI);
				}catch(Exception e){
					e.printStackTrace();
					errorMesg = new ErrorMessage();
					errorMesg.setErrormessage("Can not load owl file ");
					vecError.add(errorMesg);
					
					// record error log
					Logger logger = RadiantToolConfig.getLogger();
					logger.error(e.toString());
					
					return ERROR;
				}
			}
				
			// create wsdl manager
			DiscoveryManager wsdlmgr = new DiscoveryManager(discoveryMgr, owlIRI);
					
			// define request Name
			String _requestName = serviceName;
			if (DEBUG) System.out.println("request service name = " + _requestName + "\n");
			
			String _requestDoc = serviceDoc;
			if (DEBUG) System.out.println("request service document = " + _requestDoc + "\n");
			
			String _requestURI = null;
			
			// define operation Name
			String _operationName = operationName[0];
	        //if (DEBUG) System.out.println("Operation Name = " + deepToString(_operationName) + "\n");

	        // define functionality
			String _functionality = operationConcept[0];
			if (DEBUG) System.out.println("request operation functionality = " + _functionality + "\n");
			
			// define functionality description
			String _functionalityDes = operationText[0];
			
			
			// define input Parameter
			String[] _inParameter = listToArray(operationInputs.get(0));
			if (DEBUG) System.out.println("input Parameters:" + deepToString(_inParameter));
			String[] _inputs = new String[operationInputConcept.get(0).size()];
			for (int i = 0; i < operationInputConcept.get(0).size(); i++){
				if (DEBUG) System.out.println("the " + i + "th input concept = " + operationInputConcept.get(0).get(i));
				if (discoveryMgr.getConceptClass(operationInputConcept.get(0).get(i)) != null){
					_inputs[i] = discoveryMgr.getConceptClass(operationInputConcept.get(0).get(i)).getIRI().toString();
				}
			}
			if (DEBUG) System.out.println("input concepts:" + deepToString(_inputs));
			String[] _inputsDes = listToArray(operationInputText.get(0));
			String[] _inputTypes = listToArray(operationInputType.get(0));
			if (DEBUG) System.out.println("input Types:" + deepToString(_inputTypes) + "\n");
			MessageOBJ in = new MessageOBJ();
			List<SimpleTypeOBJ> simples = new ArrayList<SimpleTypeOBJ>();
			List<ComplexTypeOBJ> complexs = new ArrayList<ComplexTypeOBJ>();
			for (int i = 0; i < _inParameter.length; i++){
				if (_inputTypes[i].equals("complex") && mgr != null){
					ComplexTypeOBJ obj = new ComplexTypeOBJ();
					obj.setName(_inParameter[i]);
					if (_inputs[i] != null) obj.setModelReference(IRI.create(_inputs[i]));
					obj.setDescription(_inputsDes[i]);
					// construct model complex type
					OWLClass cplexCls = mgr.getConceptClass(IRI.create(_inputs[i]).toString());
					ComplexTypeOBJ model = getComplexTypeModel(cplexCls, mgr);
					obj.setSimples(model.getSimples());
					obj.setComplextypes(model.getComplextypes());
					complexs.add(obj);
				}else{
					SimpleTypeOBJ obj = new SimpleTypeOBJ();
					obj.setName(_inParameter[i]);
					if (_inputs[i] != null) obj.setModelReference(IRI.create(_inputs[i]));
					obj.setDescription(_inputsDes[i]);
					obj.setType(_inputTypes[i]);
					simples.add(obj);
				}
			}
			in.setSimpletype(simples);
			in.setComplextype(complexs);
			
			//define output Parameter
			String[] _outParameter = listToArray(operationOutputs.get(0));
			if (DEBUG) System.out.println("output Parameters:" + deepToString(_outParameter));
			String[] _outputs = new String[operationOutputConcept.get(0).size()];
			for (int i = 0; i < operationOutputConcept.get(0).size(); i++){
				if (DEBUG) System.out.println("the " + i + "th output concept = " + operationOutputConcept.get(0).get(i));
				if (discoveryMgr.getConceptClass(operationOutputConcept.get(0).get(i)) != null){
					_outputs[i] = discoveryMgr.getConceptClass(operationOutputConcept.get(0).get(i)).getIRI().toString();
				}
			}
			if (DEBUG) System.out.println("output concepts:" + deepToString(_outputs) );
			String[] _outputsDes = listToArray(operationOutputText.get(0));
			String[] _outputTypes = listToArray(operationOutputType.get(0));
			if (DEBUG) System.out.println("output Types:" + deepToString(_outputTypes) + "\n");
			MessageOBJ out = new MessageOBJ();
			simples = new ArrayList<SimpleTypeOBJ>();
			complexs = new ArrayList<ComplexTypeOBJ>();
			for (int i = 0; i < _outParameter.length; i++){
				if (_outputTypes[i].equals("complex") && mgr != null){
					ComplexTypeOBJ obj = new ComplexTypeOBJ();
					obj.setName(_outParameter[i]);
					if (_outputs[i] != null) obj.setModelReference(IRI.create(_outputs[i]));
					obj.setDescription(_outputsDes[i]);
					// construct model complex type
					OWLClass cplexCls = mgr.getConceptClass(IRI.create(_outputs[i]).toString());
					ComplexTypeOBJ model = getComplexTypeModel(cplexCls, mgr);
					obj.setSimples(model.getSimples());
					obj.setComplextypes(model.getComplextypes());
					complexs.add(obj);
				}else{
					SimpleTypeOBJ obj = new SimpleTypeOBJ();
					obj.setName(_outParameter[i]);
					if (_outputs[i] != null) obj.setModelReference(IRI.create(_outputs[i]));
					obj.setDescription(_outputsDes[i]);
					obj.setType(_outputTypes[i]);
					simples.add(obj);
				}
			}
			out.setSimpletype(simples);
			out.setComplextype(complexs);
			
			// create request template
			if (DEBUG) System.out.println("create request template ...");
			WsOpReader request = wsdlmgr.createRequest(owlIRI, _requestName, _requestDoc, _requestURI, _operationName, _functionalityDes, _functionality, in, out);
			if (DEBUG) System.out.println();
			
			// find the services whose score is more than threshold
			if (DEBUG) System.out.println("Starting to Search Web Service in database ...");
			SortValueMap<OperationInfo, Double> score;
			Iterator<String> iter;
			
			if (!owlloc.equals("")){	// no ontology 
				// search sawsdl
				SAWSDLResult = wsdlmgr.compareSAWSDLServices(request, 0.0001, baseURI);
				//score is used to put all operation score by service
				score = new SortValueMap<OperationInfo, Double>();
		        // put values
				iter = SAWSDLResult.keySet().iterator();
				if (DEBUG) System.out.println("result:");
				while (iter.hasNext()) {
					String service = iter.next();
					// put all value into score
					HashMap<String, Double> opResult = SAWSDLResult.get(service);
					for (String operation : opResult.keySet()){
						OperationInfo op = new OperationInfo();
						op.setService(service);
						op.setServicepath(urlbase + "XMLBox/SAWSDLBox/" + service);
						op.setOperation(operation);
						score.put(op, opResult.get(operation));
					}
				}
				showSawsdlResult = toArrayList(score);
			}
			
			// search wsdl
			WSDLResult = wsdlmgr.compareWSDLServices(request, 0.0001, baseURI);
			//score is used to put all operation score by service
			score = new SortValueMap<OperationInfo, Double>();
	        // put values
			iter = WSDLResult.keySet().iterator();
			if (DEBUG) System.out.println("result:");
			while (iter.hasNext()) {
				String service = iter.next();
				// put all value into score
				HashMap<String, Double> opResult = WSDLResult.get(service);
				for (String operation : opResult.keySet()){
					OperationInfo op = new OperationInfo();
					op.setService(service);
					op.setServicepath(urlbase + "XMLBox/WSDLBox/" + service);
					op.setOperation(operation);
					score.put(op, opResult.get(operation));
				}
			}
			showWsdlResult = toArrayList(score);
			
			if (DEBUG) System.out.println("Done");
		
		} catch (Exception e) {
			e.printStackTrace();
			errorMesg = new ErrorMessage();
			errorMesg.setErrormessage(e.toString());
			vecError.add(errorMesg);
			
			// record error log
			Logger logger = RadiantToolConfig.getLogger();
			logger.error(e.toString());
			
			return ERROR;
		}

		return SUCCESS;	
		
	}

	

	public void setInputType(String[] inputType) {
		this.inputType = inputType;
	}

	public String[] getInputType() {
		return inputType;
	}

	public void setInputConcept(String[] inputConcept) {
		this.inputConcept = inputConcept;
	}

	public String[] getInputConcept() {
		return inputConcept;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setOperationConcept(String[] operationConcept) {
		this.operationConcept = operationConcept;
	}

	public String[] getOperationConcept() {
		return operationConcept;
	}

	public void setVecError(Vector<ErrorMessage> vecError) {
		this.vecError = vecError;
	}

	public Vector<ErrorMessage> getVecError() {
		return vecError;
	}

	public void setOutputType(String[] outputType) {
		this.outputType = outputType;
	}

	public String[] getOutputType() {
		return outputType;
	}

	public void setOutputConcept(String[] outputConcept) {
		this.outputConcept = outputConcept;
	}

	public String[] getOutputConcept() {
		return outputConcept;
	} 

	public String[] listToArray (ArrayList<String> input){
		String[] result = new String[input.size()];
		for (int i = 0; i < input.size(); i++) result[i] = input.get(i);
		return result;
	}
	
	public static ArrayList<OperationInfo> toArrayList (SortValueMap<OperationInfo, Double> input){
		ArrayList<OperationInfo> result = new ArrayList<OperationInfo>();
		if (input != null){
			for (OperationInfo key : input.keySet()) {
				key.setScore(input.get(key));
				result.add(key);
			}
		}
		return result;
	}

	public void setOperationName(String[] operationName) {
		this.operationName = operationName;
	}

	public String[] getOperationName() {
		return operationName;
	}

	public void setOperationText(String[] operationText) {
		this.operationText = operationText;
	}

	public String[] getOperationText() {
		return operationText;
	}

	public void setInputName(String[] inputName) {
		this.inputName = inputName;
	}

	public String[] getInputName() {
		return inputName;
	}

	public void setInputText(String[] inputText) {
		this.inputText = inputText;
	}

	public String[] getInputText() {
		return inputText;
	}

	public void setOutputText(String[] outputText) {
		this.outputText = outputText;
	}

	public String[] getOutputText() {
		return outputText;
	}

	public void setOutputName(String[] outputName) {
		this.outputName = outputName;
	}

	public String[] getOutputName() {
		return outputName;
	}

	public void setOwlloc(String owlloc) {
		this.owlloc = owlloc;
	}

	public String getOwlloc() {
		return owlloc;
	}

	public void setServiceDoc(String serviceDoc) {
		this.serviceDoc = serviceDoc;
	}

	public String getServiceDoc() {
		return serviceDoc;
	}

	public void setShowSawsdlResult(ArrayList<OperationInfo> showSawsdlResult) {
		this.showSawsdlResult = showSawsdlResult;
	}

	public ArrayList<OperationInfo> getShowSawsdlResult() {
		return showSawsdlResult;
	}

	public void setShowWsdlResult(ArrayList<OperationInfo> showWsdlResult) {
		this.showWsdlResult = showWsdlResult;
	}

	public ArrayList<OperationInfo> getShowWsdlResult() {
		return showWsdlResult;
	}
	
	private ComplexTypeOBJ getComplexTypeModel(OWLClass cplexCls, OntologyManager mgr) {
		
		ComplexTypeOBJ result = new ComplexTypeOBJ();
		
		// set simple type
		List<SimpleTypeOBJ> complex_simples = new ArrayList<SimpleTypeOBJ>();
		List<ComplexTypeOBJ> complex_complex = new ArrayList<ComplexTypeOBJ>();
		
		Set<OWLClass> claz = OntologyManager.flatten(mgr.getSuperClasses(cplexCls));
		claz.add(cplexCls);
		// get all triples and inherited triples
		Set<Triple> triples = new HashSet<Triple>();
		for (OWLClass c : claz) triples.addAll(mgr.getRestrictTriples(c));
		for (Triple t : triples){
			String property_label = mgr.getPropertyLabel(t.getProperty());
			if (checkPropertyPattern(property_label)){
				OWLClass child = t.getChild();
				// check the child is complex type or not
				ComplexTypeOBJ complex = getComplexTypeModel(child, mgr);
				if (complex.getComplextypes().size() > 0 || complex.getSimples().size() > 0){
					complex_complex.add(complex);
				}else{
					SimpleTypeOBJ s = new SimpleTypeOBJ();
					s.setName(mgr.getClassLabel(child));
					s.setDescription(mgr.getClassDefinition(child));
					s.setModelReference(IRI.create(child.getIRI().toString()));
					s.setType("string");
					complex_simples.add(s);
				}
			}
		}
		result.setSimples(complex_simples);
		result.setComplextypes(complex_complex);
		return result;
	}

	private boolean checkPropertyPattern(String property_label) {
		for (String pattern : has_part_pattern){
			if (property_label.contains(pattern)) return true;
		}
		return false;
	}


}