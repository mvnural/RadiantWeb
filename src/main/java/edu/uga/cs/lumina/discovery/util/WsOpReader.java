package edu.uga.cs.lumina.discovery.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;

import edu.uga.cs.wstool.parser.sawsdl.MessageOBJ;
import edu.uga.cs.wstool.parser.sawsdl.OperationOBJ;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.sawsdl.SimpleTypeOBJ;
import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.util.QueryManager;
import edu.uga.radiant.util.RadiantToolConfig;

public class WsOpReader {

	/** wsdl instances that holds the wsdl operations
	*/
	private static Map<String, List<WsOpReader>> wsdlInstances = new HashMap<String, List<WsOpReader>>();
	private static Map<String, String> wsdlMD5Instances = new HashMap<String, String>();
	private Logger logger = RadiantToolConfig.getLogger();
	
	/** ontology discovery manager that holds the OWL file
	*/
	//private OntologyManager discoveryMgr;
	
    /** OWL file path
     */
    //private String owlURI;
    
    /** serviceName  the name of the web service
     */
    //private String serviceName;
    
    /** serviceDoc  the document of the web service
     */
    private String serviceDoc;
    
    /** serviceURI the URI  of the web service
     */
    //private String serviceURI;
    
    /** OperationName name of the operation
     */
    private String operationName;
    
    /** functionality the concept of the operation
     */
    private IRI functionality;
    
    /** description of the operation
     */
    private String functionalityDes;
    
    /** input message
     */
    private MessageOBJ input;
    
    /** output message
     */
    private MessageOBJ output;
    
    public static List<WsOpReader> getInstance(Connection conn, OntologyManager discoveryMgr, String name, String type) throws Exception{
        
    	List<WsOpReader> wsdlOps = new ArrayList<WsOpReader>();
        if (wsdlInstances.containsKey(name + "." + type)) {
        	String md5 = wsdlMD5Instances.get(name + "." + type);
        	String oldMD5 = QueryManager.getMD5(conn, name, type);
        	if (md5 != null && oldMD5 != null){
        		if (md5.equals(oldMD5)){
            		wsdlOps = wsdlInstances.get(name + "." + type);
            		return wsdlOps;
            	}
        	}
        }
        
        boolean isSAWSDL = false;
        if (type.equals("sawsdl")) isSAWSDL = true;
        wsdlOps = readWSDL(conn, name, isSAWSDL);
        wsdlInstances.put(name + "." + type, wsdlOps);
        String xml = QueryManager.getWSDL(conn, name, type);
        wsdlMD5Instances.put(name, WSDLSystem.generateMD5(xml));
        return wsdlOps;
        
    }
    
    /**
     * Constructor of WsOpReader
     */
    public WsOpReader(String _serviceDoc, String _operationName, String _functionalityDes, String _functionality,
    		MessageOBJ _input, MessageOBJ _output) 
    {
        serviceDoc = _serviceDoc;
        operationName = _operationName;
        //discoveryMgr = _discoveryMgr;
        functionality = null;
        functionalityDes = _functionalityDes;
        input = _input;
        output =_output;
    }
    
	public boolean getWsConcept(OntologyManager discoveryMgr, String _functionality, List<SimpleTypeOBJ> _inputs, List<SimpleTypeOBJ> _outputs)
    {
    	// use boolean to verify if we find the OWLClass
        boolean inputFound = true;
        boolean outputFound = true;
        boolean functionalityFound = true;
        
        // check all request input OWLClasses
        for (int p = 0; p < _inputs.size(); p++) {
        	if (discoveryMgr.getConceptClass(_inputs.get(p).getModelReference().toString()) == null){
        		//if (DEBUG) System.out.println("      The " + p + "th input " + _inputs[p] + " is not found in the ontology");
        		_inputs.get(p).setModelReference(null);
        		inputFound = false;
        	}
        }
        
        // check all request output OWLClasses
        for (int p = 0; p < _outputs.size(); p++) {
        	if (discoveryMgr.getConceptClass(_outputs.get(p).getModelReference().toString()) == null){
        		//if (DEBUG) System.out.println("      The " + p + "th input " + _inputs[p] + " is not found in the ontology");
        		_outputs.get(p).setModelReference(null);
        		outputFound = false;
        	}
        }
        
        logger.debug("\n");
        return functionalityFound && inputFound && outputFound;

    }

    /**To read the input from a SAWSDL file
     * @param connection 
     * @param isSAWSDL 
     * @throws Exception */
    public static List<WsOpReader> readWSDL(Connection connection, String filename, boolean isSAWSDL) throws Exception {        
    	ArrayList<WsOpReader> listOfOperations = new ArrayList<WsOpReader>();
        
    	Logger logger = RadiantToolConfig.getLogger();
    	
    	String _serviceName = null;
        String _serviceDoc = null;
        String _operationName = null;
        String _functionality = null;
        String _functionalityDes = null;
        MessageOBJ _input = null;
        MessageOBJ _output = null;
        
        // get xml from database
        String type = "";
        if (isSAWSDL == true){
        	type = "sawsdl";
        }else{
        	type = "wsdl";
        }
        String xml = QueryManager.getWSDL(connection, filename, type);
        
        SAWSDLParser semP = new SAWSDLParser(xml);
        if (semP.getServiceNames().size() != 0) {
            _serviceName = semP.getServiceNames().get(0);
        }
        logger.debug("serviceName = " + _serviceName);
        
        _serviceDoc = semP.getServiceDoc();
        logger.debug("serviceDoc = " + _serviceDoc);
        
        for (OperationOBJ op : semP.getAllOperations().values()) {
            _operationName = op.getName();
            
            if (op.getFunctionality() != null){
            	_functionality = op.getFunctionality().toString();
            }else{
            	_functionality = "";
            }
            
            _functionalityDes = op.getDoc();
            
            //if (DEBUG) System.out.println("   functionality(Operation name) = " + _operationName);
            //if (DEBUG) System.out.println("   functionality(Operation IRI) = " + _functionality);
            //if (DEBUG) System.out.println("   functionality(Operation text) = " + _functionalityDes);
            
            logger.debug("   searching inputs ");
            _input = op.getInput();
            
            logger.debug("   searching outputs ");
            _output = op.getOutput();
            
            // because there is no owl file from Internet URL now, we change to local file
            //_ontologyName = "edam_v12.owl";  // if there is a URL, comment it.
            
            WsOpReader reader = null;
            
            reader = new WsOpReader(_serviceDoc, _operationName, _functionalityDes, _functionality, _input, _output);
            
            listOfOperations.add(reader);
        }

        return listOfOperations;
    }
    
    /**To read the input from a SAWSDL file
     * param
     * @throws Exception */
    public static List<SimpleTypeOBJ> getAllInputSimpleType(WsOpReader op) throws Exception {        
    	
    	List<SimpleTypeOBJ> result = new ArrayList<SimpleTypeOBJ>();
    	result.addAll(SAWSDLParser.getAllSimpleType(op.getInput()));
    	return result;
    
    }
    
    /**To read the input from a SAWSDL file
     * param
     * @throws Exception */
    public static List<SimpleTypeOBJ> getAllOutputSimpleType(WsOpReader op) throws Exception {        
    	
    	List<SimpleTypeOBJ> result = new ArrayList<SimpleTypeOBJ>();
    	result.addAll(SAWSDLParser.getAllSimpleType(op.getOutput()));
    	return result;
    
    }
    
    /**To read the input from a SAWADL file
     * @throws Exception */
    public static List<WsOpReader> readSAWADL(OntologyManager discoveryMgr, String filename) throws Exception {
        
    	ArrayList<WsOpReader> listOfObjects = new ArrayList<WsOpReader>();
/*
        String _serviceDoc = null;
        String _operationName = null;
        String _functionality = null;
        String _functionalityDes = null;
        String[] _inputParameter = null;
        String[] _inputs = null;
        String[] _inputsDes = null;
        String[] _inputTypes = null;
        String[] _outputParameter = null;
        String[] _outputs = null;
        String[] _outputsDes = null;
        String[] _outputTypes = null;

        SAWADLParserDriver spd = new SAWADLParserDriver();
		spd.parse(filename);
		String[] splits= filename.split("/");
		System.out.println(splits.length-1);
		System.out.println(splits[splits.length-1]);
		System.out.println(splits[splits.length-1].indexOf(".sawadl"));
		System.out.println(splits[splits.length-1].substring(0, splits[splits.length-1].indexOf(".sawadl")));
        
        
        List<Method> methods = spd.getCompleteMethodList();
        for(Method method : methods){
        	_operationName = method.getName();
         
        	for (ModelReference ref: method.getModelReferences()) {
        		if(ref!=null){
        			if(ref.getPrefix()!=null){
        				//_ontologyName = spd.getApp().getNamespace(ref.getPrefix());
        			}
        			else if(ref.getNamespace()!=null){
        				//_ontologyName = ref.getNamespace().toString();
        			}
                _functionality = ref.getConcept();
                break;
        		}
            }
        	List<ParamImpl> paramList = new Vector();
        	paramList = method.getRequest().getParamList();
 
        	_inputParameter = new String[paramList.size()];
        	_inputs = new String[paramList.size()];
        	_inputTypes = new String[paramList.size()];
        	
        	int i=0;
        	for(Param param : paramList){
    			
    			_inputParameter[i]=param.getName();
    			_inputTypes[i]=param.getType();
    			
    			if(param.getModelreference()!=null){
    				_inputs[i]=param.getModelreference();
    			}
    			else{
    				_inputs[i]=null;
    			}
    			
    			i++;
    			
    		}
        	 
        	List<ParamImpl> paramList1 = new Vector();
        	paramList1 = method.getResponse().getParamList();
 
        	_outputParameter = new String[paramList1.size()];
        	_outputs = new String[paramList1.size()];
        	_outputTypes = new String[paramList1.size()];
        	
        	i=0;
        	for(Param param : paramList1){
        		
        		if(param.getName()!=null){
        			_outputParameter[i]=param.getName();
    			}
    			else{
    				_outputParameter[i]=null;
    			}
        		if(param.getType()!=null){
        			_outputTypes[i]=param.getType();
    			}
    			else{
    				_outputTypes[i]=null;
    			}
        	    			
    			if(param.getModelreference()!=null){
    				_outputs[i]=param.getModelreference();
    			}
    			else{
    				_outputs[i]=null;
    			}
    			
    			i++;
    			
    		}
 
        
        	WsOpReader reader = null;  
        	
            //reader = new WsOpReader(discoveryMgr, _ontologyName, _serviceName, _serviceDoc, _serviceURI, _operationName, _functionalityDes, _functionality, _inputParameter, _inputs, _inputsDes, _inputTypes, _outputParameter, _outputs, _outputsDes, _outputTypes);        
            
            listOfObjects.add(reader);
        }
*/
        return listOfObjects;
    }

    /**************************************************************************************************************************/
    /**To write to console to confirm that the input has been read properly*/
    /*
    public void writeSpec() {
        System.out.println("serviceName : " + serviceName);
        System.out.println("serviceURI : " + serviceURI);
        System.out.println("operationName : " + operationName);
        System.out.println("functionality : " + functionality);
        for (String inp : inParameter) {
            System.out.println("inputParameter : " + inp);
        }
        
        for (IRI iput : inputs) {
            System.out.println("inputs : " + iput);
        }
        
        for (String iputType : inputTypes) {
            System.out.println("input Types : " + iputType);
        }

        for (String oup : outParameter) {
            System.out.println("outputParameter : " + oup);
        }

        for (IRI oput : outputs) {
            System.out.println("outputs : " + oput);
        }

        for (String oputType : outputTypes) {
            System.out.println("output Types : " + oputType);
        }

    }
	*/
    public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setFunctionality(IRI functionality) {
		this.functionality = functionality;
	}

	public IRI getFunctionality() {
		return functionality;
	}

	public void setFunctionalityDes(String functionalityDes) {
		this.functionalityDes = functionalityDes;
	}

	public String getFunctionalityDes() {
		return functionalityDes;
	}

	public void setServiceDoc(String serviceDoc) {
		this.serviceDoc = serviceDoc;
	}

	public String getServiceDoc() {
		return serviceDoc;
	}

	public void setInput(MessageOBJ input) {
		this.input = input;
	}

	public MessageOBJ getInput() {
		return input;
	}

	public void setOutput(MessageOBJ output) {
		this.output = output;
	}

	public MessageOBJ getOutput() {
		return output;
	}

}

