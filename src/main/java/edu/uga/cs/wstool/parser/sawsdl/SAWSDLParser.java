/**
 * Copyright (c) 2012 Yung Long Li.. All rights reserved.
 *
 *
 *   Name of the File : SAWSDLParser.java .
 *   Created on : Aug 29, 2009 at 2:26:31 PM .
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *  2. Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *  3. Neither the name of the University of Georgia nor the names
 *     of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 *  CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 *  OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uga.cs.wstool.parser.sawsdl;

import edu.uga.cs.wstool.parser.xml.XMLParser;
import edu.uga.radiant.util.DataBaseConnection;
import edu.uga.radiant.util.DatabaseManager;
import edu.uga.radiant.util.QueryManager;
import edu.uga.radiant.util.WSDLConstants;

import java.io.StringReader;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.semanticweb.owlapi.model.IRI;
import org.xml.sax.InputSource;

/**
 * parse the wsdl file or xml to get whole information, ex. operation , message, complex types, simple types
 * and their documents or concept IRI, type etc.
 */
public class SAWSDLParser extends XMLParser
{

    public static final String ATTR_EXTENSIONS_SAWSDL = "attrExtensions";
    public static final String MESSAGE_NAME = "message";
    public static final String MODEL_REFERENCE_NAME = "modelReference";
    public static final String NAME_ATTRIBUTE_OPERATION = "name";
    public static final String NAME_ATTRIBUTE_MESSAGE = "name";
    public static final String WSDL_V1_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
    public static final String WSDL_V2_NAMESPACE = "http://www.w3.org/ns/wsdl";
    public final String ELEMENT = "element";
    public final String ELEMENT_TYPE = "type";
    public final String PORT_TYPE_NAME = "portType";
    public final String OPERATION_NAME = "operation";
    public static final String SAWSDL_PREFIX = "sawsdl";
    public static final String WSDL_PREFIX = "wsdl";
    public final String ELEMENT_NAME = "name";
    public final String SCHEMA = "schema";
    public final String TYPES = "types";
    public static Namespace xsdNS = null;
    public static Namespace wsdlNS = null;
    public static Namespace sawsdlNS = null;
    
    /** wsdl definition
     */
    private Definition def;
    
    /** xml document for this wsdl
     */
    private Document currentDocument;
    
    /** root element for this wsdl
     */
    private Element rootElement;
    
    /** schema element for this wsdl
     */
    private Element SchemaElem;
    
    /** is wsdl version 1
     */
    private boolean isWsdlv1;
    
    /** operations for this wsdl
     */
    private HashMap<String, OperationOBJ> operations = new HashMap<String, OperationOBJ>();
    
    /** The HashMap of complex type for convenient to get complex type
     */
    private HashMap<String, ComplexTypeOBJ> schemaComplexMap;
    
    /** The serial number to index the element of operation, message, input, output, complex type and simple type
     */
    private int id = 0;
    
	/** wsdl HashMap for element, key is wsdl id, value is element of wsdl.
     */
	private HashMap<Integer, AnnotationOBJ> annotationObjectMap = new HashMap<Integer, AnnotationOBJ>(); 
   
	/**
	 * get file url and build the sawsdl parser
	 * @param fileURL url of file
	 * @throws Exception
	 */
    public SAWSDLParser(URL fileURL) throws Exception
    {
    	super(fileURL);
    	currentDocument = super.getDoc();
    	def = generateDefinition(fileURL);
        rootElement = getRootElement(currentDocument);
        SchemaElem = getSchemaElem(currentDocument);
        setAllNamespaces(rootElement);
        setWSDLVersion();
        schemaComplexMap = getAllComplexTypes(SchemaElem);
        operations = setAllOperations();
    }

    /**
     * get xml string and build the sawsdl parser
     * @param xml string xml
     * @throws Exception
     */
    public SAWSDLParser(String xml) throws Exception {
    	super(xml);
    	currentDocument = super.getDoc();
    	def = generateDefinition(xml);
        rootElement = getRootElement(currentDocument);
        SchemaElem = getSchemaElem(currentDocument);
        setAllNamespaces(rootElement);
        setWSDLVersion();
        schemaComplexMap = getAllComplexTypes(SchemaElem); 
        operations = setAllOperations();
	}
    
    /**
     * get jdom document and build the sawsdl parser
     * @param doc jdom document
     * @throws Exception
     */
    public SAWSDLParser(Document doc) throws Exception{
    	super(doc);
    	currentDocument = super.getDoc();
    	def = generateDefinition(updateToXml());
        rootElement = getRootElement(currentDocument);
        SchemaElem = getSchemaElem(currentDocument);
        setAllNamespaces(rootElement);
        setWSDLVersion();
        schemaComplexMap = getAllComplexTypes(SchemaElem);
        operations = setAllOperations();
	}
    
    /**
     * return the current Document
     * @return Document: current Document
     */
    public Document getCurrentDocument()
    {
		return currentDocument;
    }
    
    /**
     * given jdom document, return the schema Element
     * @param Document: document of xml
     * @return Element: type schema element
     */
    public static Element getSchemaElem(Document doc)
    {
        Element schemaEle = null;      
        
        Element root = getRootElement(doc);
        
        // get types schema element
        xsdNS = Namespace.getNamespace("http://www.w3.org/2001/XMLSchema");
        wsdlNS = root.getNamespace("wsdl");
        sawsdlNS = root.getNamespace("sawsdl");

        schemaEle = root.getChild("types", wsdlNS).getChild("schema", xsdNS);
        String preXsdNs = schemaEle.getNamespacePrefix();
        xsdNS = Namespace.getNamespace(preXsdNs, "http://www.w3.org/2001/XMLSchema");
        
        return schemaEle;
    }

    /***final String NAME_ATTRIBUTE_MESSAGE = "name";
     * This method is used to generate the required definition object.
     * @param String xml
     * @return Definition
     * @throws Exception
     */
	private Definition generateDefinition(String xml) throws Exception {
		
		DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = docfactory.newDocumentBuilder();
		org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(xml))); 
		
		WSDLFactory factory = null;
		try {
			factory = WSDLFactory.newInstance();
		} catch (WSDLException ex) {
			throw new Exception("Unable to create instance of WSDLFactory\n" + ex.getMessage());
		}
		WSDLReader reader = factory.newWSDLReader();
		Definition def = null;
		try {
			def = reader.readWSDL(document.getDocumentURI(), new InputSource(new StringReader(xml)));
		} catch (WSDLException ex) {
			throw new Exception("Error parsing WSDL file\n" + ex.toString());
		}
		return def;
	}

	/***final String NAME_ATTRIBUTE_MESSAGE = "name";
     * This method is used to generate the required definition object.
     * @param fileName
     * @return Definition
     * @throws Exception
     */
    public static Definition generateDefinition(URL fileURL) throws Exception
    {
		WSDLFactory factory = null;
		try {
			factory = WSDLFactory.newInstance();
		} catch (WSDLException ex) {
			throw new Exception("Unable to create instance of WSDLFactory\n"
					+ ex.getMessage());
		}
		WSDLReader reader = factory.newWSDLReader();
		Definition def = null;
		try {
			def = reader.readWSDL(fileURL.toURI().toString());
		} catch (WSDLException ex) {
			throw new Exception("Error parsing WSDL file\n" + ex.getMessage());
		}
		return def;
    }
    
    /***
     * This method is used to get the definition.
     * @param doc
     * @return Element
     */
    public Definition getDefinition()
    {
        return def;
    }

    /***
     * This method is used to get the root element of the document.
     * @param doc
     * @return Element
     */
    public static Element getRootElement(Document doc)
    {
        return doc.getRootElement();
    }

    /****
     * This method is used to set all the namespaces
     * @param rootElement
     */
    public void setAllNamespaces(Element rootElement)
    {
    	sawsdlNS = rootElement.getNamespace(SAWSDL_PREFIX);
    	// verify the following Namespace
    	if (sawsdlNS == null) sawsdlNS = Namespace.getNamespace(SAWSDL_PREFIX, "http://www.w3.org/ns/sawsdl");
    	wsdlNS = rootElement.getNamespace(WSDL_PREFIX);
        xsdNS = Namespace.getNamespace("http://www.w3.org/2001/XMLSchema");
    }

    /***
     * This is used to set the isWsdlv1.
     */
    public void setWSDLVersion()
    {
        if (wsdlNS == null || wsdlNS.getURI() == null) return;
        if ((wsdlNS.getURI()).equals(WSDL_V1_NAMESPACE))
        {
            isWsdlv1 = true;
        }
        else
        {
            isWsdlv1 = false;
        }
    }
    
    /***
     * This is used to set the current id.
     */
    public void setId(int id) {
		this.id = id;
	}

    /***
     * This is used to get the current id.
     */
	public int getId() {
		return id;
	}
    
    /***
     * This is used to return if wsdl is isWsdlv1.
     */
    public boolean isWsdlV1()
    {
    	return isWsdlv1;
    }
    
    /***
     * return operations.
     * @return operations 
     */ 
    public HashMap<String, OperationOBJ> getAllOperations(){
    	return operations;
    }

    /***
     * This method return all the operations in a given sawsdl.
     * @param id 
     * @param eleMap 
     * @return
     * @throws JDOMException 
     */ 
	@SuppressWarnings("rawtypes")
	public HashMap<String, OperationOBJ> setAllOperations() throws JDOMException
    {       
        Map portTypes = def.getPortTypes();
        HashMap<String, OperationOBJ> ops = new HashMap<String, OperationOBJ>();
        for (Iterator i = portTypes.keySet().iterator(); i.hasNext();)
        {
            PortType pt = (PortType) portTypes.get(i.next());
            if (pt.getOperations() != null)
            {
            	Element portType = seekChildSingleNode(rootElement, "portType", true);
        		pt.getOperations();
            	for (Object o : pt.getOperations()){
            		OperationOBJ obj = new OperationOBJ();
            		Operation op1 = (Operation) o;
            		// set element
            		Element ele = seekChildSingleNode(portType, op1.getName(), false);
            		obj.setElement(ele);
            		// set wsdl id
    				obj.setId(id);
    				annotationObjectMap.put(id, obj);
    				id++;
            		// set name
            		obj.setName(op1.getName());
            		// set doc
            		if (op1.getDocumentationElement() != null) obj.setDoc(op1.getDocumentationElement().getTextContent());
            		// set functionality
            		if (getOpModelReference(op1).size() != 0) obj.setFunctionality(IRI.create(getOpModelReference(op1).iterator().next()));
            		// set input MessageOBJ
            		Message im = getOpInMessage(op1);
            		MessageOBJ input = getMessage(im);
            		obj.setInput(input);
            		// set output MessageOBJ
            		Message om = getOpOutMessage(op1);
                    MessageOBJ output = getMessage(om);
            		obj.setOutput(output);
            		ops.put(obj.getName(), obj);
            	}
            }
        }
        return ops;
    }

    /**
     * This method is used to get the operation name of the model reference.
     * @param op
     * @return
     */
    public String getOperationName(Operation op)
    {
        return op.getName();
    }

    /**
     * This method is used to return services in a wsdl.
     * @return
     */
    public List<String> getServiceNames()
    {
        List<String> serviceNames=new ArrayList<String>();
        for(QName service:getServiceQNames())
        {
            serviceNames.add(service.getLocalPart());
        }
        return serviceNames;
    }
    
    /**
     * This method is used to return services document.
     * @return String service document
     */
    public String getServiceDoc() {
    	org.w3c.dom.Element defDoc = def.getDocumentationElement();
        String doc = "";
        if(defDoc != null){
            doc = defDoc.getTextContent();
            if(doc == null)
                doc = "";
        }
		return doc;
	}

    @SuppressWarnings("rawtypes")
	public List<QName> getServiceQNames()
    {
        List<QName> serviceQNames = new ArrayList<QName>();
        Map serviceMap = def.getServices();
        Iterator serviceIt = serviceMap.keySet().iterator();
        while(serviceIt.hasNext())
        {
            QName serviceKey = (QName) serviceIt.next();
            serviceQNames.add(serviceKey);
        }
        return serviceQNames;
    }

    /**
     * This method is used to get the output message of a operation.
     * @param op
     * @return
     */
    public Message getOpOutMessage(Operation op)
    {
        return (Message) op.getOutput().getMessage();
    }

    /**
     * This method is used to get the input message of the operation.
     * @param op
     * @return
     */
    public Message getOpInMessage(Operation op)
    {
        return (Message) op.getInput().getMessage();
    }

    /**
     * This method is used to get the message name of the  message.
     * @param m
     * @return
     */
    public String getMessageName(Message m)
    {
        return m.getQName().getLocalPart();
    }

    /**
     * This method is used to get the model refernces of the operation.
     * @param op
     * @return
     */
    @SuppressWarnings("unchecked")
	public Set<String> getOpModelReference(Operation op)
    {
        Set<String> modelRef = new HashSet<String>();
        List<Element> portTypes = rootElement.getChildren(PORT_TYPE_NAME, wsdlNS);
        for (Element portType : portTypes)
        {
            List<Element> operations = portType.getChildren(OPERATION_NAME, wsdlNS);
            for (Element operation : operations)
            {
                String operationName = operation.getAttributeValue(NAME_ATTRIBUTE_OPERATION);
                if (operationName.equals(getOperationName(op)))
                {
                    if (isWsdlv1)
                    {
                        List<Element> sawsdlExtensions = operation.getChildren(ATTR_EXTENSIONS_SAWSDL, sawsdlNS);
                        for (Element sawsdlExtension : sawsdlExtensions)
                        {
                            if (sawsdlExtension != null)
                            {
                                String tempModelRef = sawsdlExtension.getAttributeValue(MODEL_REFERENCE_NAME, sawsdlNS);
                                if (tempModelRef != null)
                                {
                                    modelRef.add(tempModelRef);
                                }
                            }
                        }
                    }
                    else
                    {
                        String tempModelRef = operation.getAttributeValue(MODEL_REFERENCE_NAME, sawsdlNS);
                        if (tempModelRef != null)
                        {
                            modelRef.add(tempModelRef);
                        }
                    }
                }
            }
        }
        return modelRef;
    }
    
    /**
     * This method is used to get the model reference of the message.
     * @param eleMap 
     * @param id 
     * @param m
     * @return
     */
    public MessageOBJ getMessage(Message ms) throws JDOMException
    {
        MessageOBJ messageobj = null;
        messageobj = getMessageOBJ(getMsElemName(ms), getMessageName(ms));
        return messageobj;
    }
    
    /**
     * given element name for xmlschema if it's complex type, then return a list of all sub element name
     * @param MsElemNames
     * @param String message name
     * @param id 
     * @param eleMap 
     * @return MessageOBJ
     * @throws JDOMException
     */
    public MessageOBJ getMessageOBJ(List<String> MsElemNames, String msName) throws JDOMException
    {
        HashMap<String, ComplexTypeOBJ> complexMap = getAllComplexTypes();
        MessageOBJ messageobj = new MessageOBJ();
        // get message element
        Element message = seekChildSingleNode(rootElement, msName, false);
        
        // set name
        messageobj.setName(message.getAttributeValue("name"));
		// set element
        messageobj.setElement(message);
        // set wsdl id
        messageobj.setId(id);
        annotationObjectMap.put(id, messageobj);
		id++;
        
        List<ComplexTypeOBJ> complexs = new ArrayList<ComplexTypeOBJ>();
        List<SimpleTypeOBJ> simples = new ArrayList<SimpleTypeOBJ>();
        for (String eleName : MsElemNames){
        	Element MsEle = seekChildSingleNode(SchemaElem, eleName, false);
        	List<Element> MsEleNodes = seekChildrenNodes(MsEle, "element");
        	for (Element MsEleNode : MsEleNodes){
        		// get the type name of element
        		String typeName = MsEleNode.getAttributeValue("type");
        		// check if it starts with "tns:"
        		if (typeName != null && typeName.startsWith("tns:")) typeName = typeName.substring(4);
        		// check if it is complex type
        		if (complexMap.keySet().contains(typeName)){	// is complex type
        			ComplexTypeOBJ model = complexMap.get(typeName);
					ComplexTypeOBJ newOBJ = new ComplexTypeOBJ();
					// set element
					newOBJ.setElement(MsEleNode);
					// set wsdl id
					newOBJ.setId(id);
					annotationObjectMap.put(id, newOBJ);
					id++;
					// set name
					newOBJ.setName(MsEleNode.getAttributeValue("name"));
					// set description
					if (!getAnnotation(MsEleNode).equals("")){
						newOBJ.setDescription(getAnnotation(MsEleNode));
					}else{
						newOBJ.setDescription(model.getDescription());
					}
					// set concept
					if (sawsdlNS != null){
						if (MsEleNode.getAttributeValue("modelReference", sawsdlNS) != null){
							newOBJ.setModelReference(IRI.create(MsEleNode.getAttributeValue("modelReference", sawsdlNS)));
						}else{
							newOBJ.setModelReference(model.getModelReference());
						}
						if (MsEleNode.getAttributeValue("liftingSchemaMapping", sawsdlNS) != null){
							newOBJ.setLiftingSchemaMapping(IRI.create(MsEleNode.getAttributeValue("liftingSchemaMapping", sawsdlNS)));
						}else{
							newOBJ.setLiftingSchemaMapping(model.getLiftingSchemaMapping());
						}
						if (MsEleNode.getAttributeValue("loweringSchemaMapping", sawsdlNS) != null){
							newOBJ.setLoweringSchemaMapping(IRI.create(MsEleNode.getAttributeValue("loweringSchemaMapping", sawsdlNS)));
						}else{
							newOBJ.setLoweringSchemaMapping(model.getLoweringSchemaMapping());
						}
					}
	                // set complex type list
					newOBJ.setComplextypes(model.getComplextypes());
					// set simple type list
					newOBJ.setSimples(model.getSimples());
					complexs.add(newOBJ);
        		}else{	// is simple type
            		SimpleTypeOBJ simple = new SimpleTypeOBJ();
            		// set element
            		simple.setElement(MsEleNode);
            		// set wsdl id
            		simple.setId(id);
            		annotationObjectMap.put(id, simple);
					id++;
					// set name
					simple.setName(MsEleNode.getAttributeValue("name"));
					// set description
					simple.setDescription(getAnnotation(MsEleNode));
					// set type
	            	String type = MsEleNode.getAttributeValue("type");
	            	if (type == null) type = "";
	                type = type.contains(":") ? type.substring(type.indexOf(":")+1) : type;
	                simple.setType(type);
	                // set modelReference and schema mapping
	                if (sawsdlNS != null){
	                	if (MsEleNode.getAttributeValue("modelReference", sawsdlNS) != null) simple.setModelReference(IRI.create(MsEleNode.getAttributeValue("modelReference", sawsdlNS)));
	                	if (MsEleNode.getAttributeValue("liftingSchemaMapping", sawsdlNS) != null) simple.setLiftingSchemaMapping(IRI.create(MsEleNode.getAttributeValue("liftingSchemaMapping", sawsdlNS)));
	                	if (MsEleNode.getAttributeValue("loweringSchemaMapping", sawsdlNS) != null) simple.setLoweringSchemaMapping(IRI.create(MsEleNode.getAttributeValue("loweringSchemaMapping", sawsdlNS)));
	                }
	                // set required 
	                String nillable = MsEleNode.getAttributeValue("nillable");
	                if (nillable == null) nillable = "";
	                if (nillable.equals("true")) simple.setRequired(false);
	                simples.add(simple);
            	}
        	}
        	
        }
        messageobj.setComplextype(complexs);
        messageobj.setSimpletype(simples);
        return messageobj;
    }
    
    /**
     * return a HashMap of hierarchy of complex type
     * @return HashMap<String, ComplexTypeOBJ>: key is complex type name, value is complex type object
     * @throws JDOMException 
     */
    public HashMap<String, ComplexTypeOBJ> getAllComplexTypes() throws JDOMException {
    	return schemaComplexMap;
    }
    
    /**
     * return a HashMap of hierarchy of complex type
     * @param Elememt schema element
     * @param int the index id
     * @param HashMap the HashMap to store the id and element
     * @return HashMap<String, ComplexTypeOBJ>: key is complex type name, value is complex type object
     * @throws JDOMException 
     */
	private HashMap<String, ComplexTypeOBJ> getAllComplexTypes(Element schemaEle) throws JDOMException {
		
		HashMap<String, ComplexTypeOBJ> result = new HashMap<String, ComplexTypeOBJ>();
		Element typeElem = null;
		List<Element> complexTypes = null;
		typeElem = seekChildSingleNode(rootElement, "types", true);
		complexTypes = seekChildrenNodes(typeElem, "complexType");
		ArrayList<String> typeNameIndex = new ArrayList<String>();
		// collect all complex type name for check
		for (Element ele : complexTypes){
			typeNameIndex.add(ele.getAttributeValue("name"));
		}
		ArrayList<Element> uncheckEle = new ArrayList<Element>();
		uncheckEle.addAll(complexTypes);
		while (uncheckEle.size() != 0){
			ArrayList<Element> checkedEle = new ArrayList<Element>();
			for (Element ele : uncheckEle){
				ComplexTypeOBJ obj = new ComplexTypeOBJ();
				List<ComplexTypeOBJ> complexList = new ArrayList<ComplexTypeOBJ>();
				// set element
				obj.setElement(ele);
				// set wsdl id
				obj.setId(id);
				annotationObjectMap.put(id, obj);
				id++;
				// get complex name
				obj.setName(ele.getAttributeValue("name"));
				// get complex description
				obj.setDescription(getAnnotation(ele));
				// get complex concept
				if (sawsdlNS != null){
					if (ele.getAttributeValue("modelReference", sawsdlNS) != null) obj.setModelReference(IRI.create(ele.getAttributeValue("modelReference", sawsdlNS)));
					if (ele.getAttributeValue("liftingSchemaMapping", sawsdlNS) != null) obj.setLiftingSchemaMapping(IRI.create(ele.getAttributeValue("liftingSchemaMapping", sawsdlNS)));
                	if (ele.getAttributeValue("loweringSchemaMapping", sawsdlNS) != null) obj.setLoweringSchemaMapping(IRI.create(ele.getAttributeValue("loweringSchemaMapping", sawsdlNS)));
                }
				// get list simple types or complex types
				List<Element> sequenceEles = seekChildrenNodes(ele, "element");
				// create sequence simple type list
				List<SimpleTypeOBJ> sequence = new ArrayList<SimpleTypeOBJ>();
				boolean checked = true;
				for (Element e : sequenceEles){
					// get the type name of element
	        		String typeName = e.getAttributeValue("type");
	        		// check if it starts with "tns:"
	        		if (typeName != null && typeName.startsWith("tns:")) typeName = typeName.substring(4);
	        		if (typeNameIndex.contains(typeName)){	// is complex type
						if (result.get(typeName) == null){	// sequence include other complex type and not check yet
							checked = false;
							break;
						}else{
							// if it has been checked, then add into complex type list
							ComplexTypeOBJ model = result.get(typeName);
							ComplexTypeOBJ newOBJ = new ComplexTypeOBJ();
							// set element
							newOBJ.setElement(e);
							// set wsdl id
							newOBJ.setId(id);
							annotationObjectMap.put(id, newOBJ);
							id++;
							// set name
							newOBJ.setName(e.getAttributeValue("name"));
							// set description
							if (!getAnnotation(e).equals("")){
								newOBJ.setDescription(getAnnotation(e));
							}else{
								newOBJ.setDescription(model.getDescription());
							}
							// set concept
							if (sawsdlNS != null){
								if (e.getAttributeValue("modelReference", sawsdlNS) != null){
									newOBJ.setModelReference(IRI.create(e.getAttributeValue("modelReference", sawsdlNS)));
								}else{
									newOBJ.setModelReference(model.getModelReference());
								}
								if (e.getAttributeValue("liftingSchemaMapping", sawsdlNS) != null){
									newOBJ.setLiftingSchemaMapping(IRI.create(e.getAttributeValue("liftingSchemaMapping", sawsdlNS)));
								}else{
									newOBJ.setLiftingSchemaMapping(model.getLiftingSchemaMapping());
								}
								if (e.getAttributeValue("loweringSchemaMapping", sawsdlNS) != null){
									newOBJ.setLoweringSchemaMapping(IRI.create(e.getAttributeValue("loweringSchemaMapping", sawsdlNS)));
								}else{
									newOBJ.setLoweringSchemaMapping(model.getLoweringSchemaMapping());
								}
							}
			                // set complex type list
							newOBJ.setComplextypes(model.getComplextypes());
							// set simple type list
							newOBJ.setSimples(model.getSimples());
							complexList.add(newOBJ);
						}
					}else{	// is simple type
						SimpleTypeOBJ simple = new SimpleTypeOBJ();
						// set element
						simple.setElement(e);
						// set wsdl id
						simple.setId(id);
						annotationObjectMap.put(id, simple);
						id++;
						// set name
						simple.setName(e.getAttributeValue("name"));
						// set description
						simple.setDescription(getAnnotation(e));
						// set type
		            	String type = e.getAttributeValue("type");
		                type = type.contains(":") ? type.substring(type.indexOf(":")+1) : type;
		                simple.setType(type);
		                // get concept
		                if (sawsdlNS != null){
		                	if (e.getAttributeValue("modelReference", sawsdlNS) != null) simple.setModelReference(IRI.create(e.getAttributeValue("modelReference", sawsdlNS)));
		                	if (e.getAttributeValue("liftingSchemaMapping", sawsdlNS) != null) simple.setLiftingSchemaMapping(IRI.create(e.getAttributeValue("liftingSchemaMapping", sawsdlNS)));
		                	if (e.getAttributeValue("loweringSchemaMapping", sawsdlNS) != null) simple.setLoweringSchemaMapping(IRI.create(e.getAttributeValue("loweringSchemaMapping", sawsdlNS)));
		                }
		                // set required 
		                String nillable = e.getAttributeValue("nillable");
		                if (nillable == null) nillable = "";
		                if (nillable.equals("true")) simple.setRequired(false);
		                
		                sequence.add(simple);
					}
	            }
				if (checked == true){
					obj.setComplextypes(complexList);
					obj.setSimples(sequence);
					result.put(ele.getAttributeValue("name"), obj);
					checkedEle.add(ele);
				}
			}
			uncheckEle.removeAll(checkedEle);
		}
		return result;
	}
    
    /**
     * Given message , get the list of element name of all parts of the message
     * @param Message
     * @return the list of element name of all parts of the message
     */
    @SuppressWarnings("rawtypes")
	public List<String> getMsElemName(Message ms)
    {
        Map partsMap = ms.getParts();
        if (partsMap == null)
        {
            return null;
        }
        Iterator it = partsMap.keySet().iterator();
        List<String> msEleNameList = new ArrayList<String>();
        while (it.hasNext())
        {
            Part pa = (Part) partsMap.get(it.next());
            if(pa != null)
            {
                if(pa.getElementName() != null)
                {
            		String temp = pa.getElementName().getLocalPart();
            		//System.out.println("      message elem name = " + temp);
            		msEleNameList.add(temp);
                }
                else if (pa.getTypeName()!= null)
                {
                    String temp = pa.getTypeName().getLocalPart();
                    //System.out.println("      message elem name = " + temp);
                    msEleNameList.add(temp);
                }
            }
        }
        return msEleNameList;
    }
    
    /**
     * Given MessageOBJ , get the list of simple types in this MessageOBJ
     * @param MessageOBJ
     * @return the list of simple types in this MessageOBJ
     */
    public static List<SimpleTypeOBJ> getAllSimpleType(MessageOBJ ms) throws Exception {        
    	
    	List<ComplexTypeOBJ> next = new ArrayList<ComplexTypeOBJ>();
    	List<SimpleTypeOBJ> result = new ArrayList<SimpleTypeOBJ>();
    	result.addAll(ms.getSimpletype());
    	next = ms.getComplextype();
    	while (next.size() > 0){
    		List<ComplexTypeOBJ> temp = new ArrayList<ComplexTypeOBJ>();
    		for (ComplexTypeOBJ plex : next){
    			temp.addAll(plex.getComplextypes());
    			result.addAll(plex.getSimples());
    		}
    		next = temp;
    	}
    	return result;
    	
    }
    
    /**
     * Given ComplexTypeOBJ , get the list of simple types in this ComplexTypeOBJ
     * @param ComplexTypeOBJ
     * @return the list of simple types in this ComplexTypeOBJ
     */
    public static List<SimpleTypeOBJ> getAllSimpleType(ComplexTypeOBJ complex) throws Exception {        
    	
    	List<ComplexTypeOBJ> next = new ArrayList<ComplexTypeOBJ>();
    	List<SimpleTypeOBJ> result = new ArrayList<SimpleTypeOBJ>();
    	result.addAll(complex.getSimples());
    	next.addAll(complex.getComplextypes());
    	while (next.size() > 0){
    		List<ComplexTypeOBJ> temp = new ArrayList<ComplexTypeOBJ>();
    		for (ComplexTypeOBJ plex : next){
    			temp.addAll(plex.getComplextypes());
    			result.addAll(plex.getSimples());
    		}
    		next = temp;
    	}
    	return result;
    	
    }
    
    /**
     * Print simple type
     * @param String indent
     * @param SimpleTypeOBJ
     */
    public static void printSimpleType(String indent, List<SimpleTypeOBJ> simples){
    	System.out.print(indent + "   simple type name = ");
    	boolean start = true;
    	for (SimpleTypeOBJ simple : simples){
    		if (start == true){
    			System.out.print(simple.getName());
    			start = false;
    		}else{
    			System.out.print(" ," + simple.getName());
    		}
    	}
    	System.out.println();
    	System.out.print(indent + "   simple type concept = ");
    	start = true;
    	for (SimpleTypeOBJ simple : simples){
    		if (start == true){
    			System.out.print(simple.getModelReference());
    			start = false;
    		}else{
    			System.out.print(" ," + simple.getModelReference());
    		}
    	}
    	System.out.println();
    	System.out.print(indent + "   simple type doc = ");
    	start = true;
    	for (SimpleTypeOBJ simple : simples){
    		if (start == true){
    			System.out.print(simple.getDescription());
    			start = false;
    		}else{
    			System.out.print(" ," + simple.getDescription());
    		}
    	}
    	System.out.println();
    	System.out.print(indent + "   simple type required = ");
    	start = true;
    	for (SimpleTypeOBJ simple : simples){
    		if (start == true){
    			System.out.print(simple.isRequired());
    			start = false;
    		}else{
    			System.out.print(" ," + simple.isRequired());
    		}
    	}
    	System.out.println();
    }

    /**
     * Print complex type
     * @param String indent
     * @param ComplexTypeOBJ
     */
    public static void printComplexType(String indent, ComplexTypeOBJ obj){
    	System.out.println(indent + "complex name:" + obj.getName() + "----------------");
    	System.out.println(indent + "   complex description = " + obj.getDescription());
    	System.out.println(indent + "   complex concept = " + obj.getModelReference());
    	printSimpleType(indent, obj.getSimples());
    	indent = indent + "   ";
    	for (ComplexTypeOBJ obj1 : obj.getComplextypes()){
    		printComplexType(indent, obj1);
    	}
    }
    
    /**
     * get the element HashMap
     * @return HashMap
     */
	public HashMap<Integer, AnnotationOBJ> getAnnotationObjectMap() {
		return annotationObjectMap;
	}
    
	/**
     * annotate WSDL operation
     * @param Element operation element
     * @param String attribute
     * @param String the value which is going to be annotated 
     * @return String message of success or fail
     */
	public String annotateWSDLOperaion(Element ele, String attr, String value, Namespace ns){
        String msg = "";
        if(ele != null){
            Element extAttr = ele.getChild(WSDLConstants.attrExtensions, ns);
            if(extAttr != null){
            	msg = addAnnotation(extAttr, attr, value, ns);
            }
            else
            {
                extAttr = new Element(WSDLConstants.attrExtensions, ns);
                msg = addAnnotation(extAttr, attr, value, ns);
                ele.addContent(extAttr);
            }
        }else{
            msg = "Error: element is not found";
        }
        return msg;
    }
    
	/**
     * annotate WSDL simple type
     * @param Element simple type element
     * @param String attribute
     * @param String the value which is going to be annotated 
     * @return String message of success or fail
     */
	public String annotateWSDLSimpleType(Element ele, String attr, String value, Namespace ns){
		String msg = addAnnotation(ele, attr, value, ns);
		return msg;
	}
	
	/**
     * annotate WSDL complex type
     * @param Element complex type element
     * @param String attribute
     * @param String the value which is going to be annotated 
     * @return String message of success or fail
     */
	public String annotateWSDLComplexType(Element ele, String attr, String value, Namespace ns){
		String msg = addAnnotation(ele, attr, value, ns);
		return msg;
	}
	
	/**
     * remove WSDL operation annotation
     * @param Element 
     * @param String attribute
     * @param String annotated value which is going to be removed 
     * @return String message of success or fail
     */
	public String removeSAWSDLOperationAnnotation(Element ele, String attr, String value){
		String msg = "";
		if(ele != null){
			if (isWsdlv1){
				Element extAttr = ele.getChild(WSDLConstants.attrExtensions, sawsdlNS);
	            msg = removeAnnotation(extAttr, attr, value, sawsdlNS);
			}else{
				// not implemented
			}
        }
		return msg;
	}
	
	/**
     * remove WSDL annotation
     * @param Element 
     * @param String attribute
     * @param String annotated value which is going to be removed 
     * @return String message of success or fail
     */
	public String removeSAWSDLAnnotation(Element ele, String attr, String value){
		String msg = removeAnnotation(ele, attr, value, sawsdlNS);
		return msg;
	}
	
	/**
     * get the list of leaf node wsdl elements which is correspond to the local name
     * @param Element: parent element
     * @param String: the target elements' local name
     * @return List<Element>: list of elements
     */
    public static List<Element> seekChildrenNodes(Element elem, String localName) {
    	List<Element> subelems = new ArrayList<Element>();
    	@SuppressWarnings("unchecked")
		List<Element> temp = elem.getChildren();
    	while(temp.size() != 0){
    		List<Element> nextLevel = new ArrayList<Element>();
    		for (Object e : temp){
    			if (((Element)e).getName().equalsIgnoreCase("sequence")){
    				subelems.addAll(seekSequenceLeafNodes((Element)e, localName));
    				continue;
    			}
    			if (((Element)e).getChildren().size() > 0){
    				for (Object child : ((Element)e).getChildren()){
    					nextLevel.add((Element)child);
    				}
    			}
    			if (((Element)e).getName().equals(localName)){
    				if (((Element)e).getAttributeValue("name") != null){
    					subelems.add((Element)e);
    				}
    			}
    		}
    		temp = nextLevel;
    	}
		return subelems;
    }
    
    /**
     * get the list of leaf wsdl elements of complex type which is correspond to the local name
     * @param Element: complexType element
     * @param String: the target elements' local name
     * @return List<Element>: list of elements
     */
    public static List<Element> seekSequenceLeafNodes(Element elem, String localName) {
    	List<Element> subelems = new ArrayList<Element>();
    	//boolean ignore = false;
    	@SuppressWarnings("unchecked")
		List<Element> temp = elem.getChildren();
    	while(temp.size() != 0){
    		List<Element> nextLevel = new ArrayList<Element>();
    		for (Object e : temp){
    			//ignore = false;
    			if (((Element)e).getChildren().size() > 0){
    				
    				for (Object child : ((Element)e).getChildren()){
    					//if (((Element)child).getName().equalsIgnoreCase("complexType")) {
    						
    						
    						//System.out.println("e name = " + ((Element)e).getAttributeValue("name"));
    						//System.out.println("child = " + (Element)child);
    						//System.out.println("name = " + ((Element)child).getName());
    						
    						
    						
    						
    						//ignore = true; 
    					//}
    					nextLevel.add((Element)child);
    				}
    			}
    			if (((Element)e).getName().equals(localName)){
    				if (((Element)e).getAttributeValue("name") != null){
    					if (((Element)e).getAttributeValue("name") != null){
    						if (((Element)e).getAttributeValue("type") != null) subelems.add((Element)e);
    					}
    					
    					
    					//if (ignore == false) subelems.add((Element)e);
    				}
    			}
    		}
    		temp = nextLevel;
    	}
		return subelems;
    }
	
    /*
    public Set<String> getModelReference(Message m)
    {
        Set<String> modelRef = new HashSet<String>();

        //you really don't need modelref for an operation.
        List<Element> messages = rootElement.getChildren(MESSAGE_NAME, wsdlNamespace);
        for (Element message : messages)
        {
            String name = message.getAttributeValue(NAME_ATTRIBUTE_MESSAGE);
            //System.out.println("the name of the message is "+name);
            if (name.equals(getMessageName(m)))
            {
                String tempModelRef = message.getAttributeValue(MODEL_REFERENCE_NAME, sawsdlNamespace);
                modelRef.add(tempModelRef);
                //System.out.println("the model ref for the message is "+tempModelRef);
            }
        }


        return modelRef;
    }
    
    public List<ServiceImpl> getServices()
    {
        List<ServiceImpl> services=new ArrayList<ServiceImpl>();
        Map serviceMap = def.getServices();
        Iterator serviceIt = serviceMap.keySet().iterator();
        while(serviceIt.hasNext())
        {
            QName serviceKey=(QName)serviceIt.next();
            ServiceImpl value=(ServiceImpl) serviceMap.get(serviceKey);
            services.add(value);
        }
        return services;

    }

    public URI getServiceURI(ServiceImpl service)
    {
        URI uri = null;
        return uri;
    }
    
    public List<PortImpl> getPorts(ServiceImpl s)
    {
        List<PortImpl> ports=new ArrayList<PortImpl>();
        Map portMap = s.getPorts();
        Iterator portIt=portMap.keySet().iterator();
        while(portIt.hasNext())
        {
            QName portKey=(QName)portIt.next();
            PortImpl value=(PortImpl) portMap.get(portKey);
            ports.add(value);
        }

        return ports;
    }
	
    
    public void getBindings()
    {
        List<QName> bindings = new ArrayList<QName>();
        Map bindingMap = def.getBindings();
        Iterator bindingIt = bindingMap.keySet().iterator();
        while(bindingIt.hasNext())
        {
            QName bindingKey=(QName) bindingIt.next();
            BindingImpl value=(BindingImpl) bindingMap.get(bindingKey);
            //System.out.println("the binding key is "+bindingKey.getLocalPart());//+" and its value is "+value);
        }
    }
    */
    
    
    /**
     * for test
     */
    public static void main(String[] args) throws Exception {
    	
    	// access by file system
    	//URL fileURL = new URL("file:/D:/wublast.sawsdl");
    	//SAWSDLParser semP = new SAWSDLParser(fileURL);
    	
    	// access by database
        DatabaseManager mgr = new DatabaseManager();
        Connection conn = mgr.getConnection();
    	String filename = "emboss_sixpack.wsdl";
    	String xml = QueryManager.getWSDL(conn, filename, "wsdl");
    	SAWSDLParser semP = new SAWSDLParser(xml);
    	
    	
    	String indent = "";
    	for (OperationOBJ op : semP.getAllOperations().values()) {
    		
    		System.out.println("operation = " + op.getName());
    		
    		indent = "";
    		MessageOBJ input = op.getInput();
            /*
            // get all leaf nodes of input
            for (SimpleTypeOBJ simple : WsOpReader.getAllSimpleType(input)){
            	System.out.println("simple name = " + simple.getName());
            	System.out.println("simple description = " + simple.getDescription());
            	System.out.println("simple type = " + simple.getType());
            	System.out.println("simple IRI = " + simple.getConcept());
            }
            System.out.println("************************************");
            */
            System.out.println("input = " + input.getName());
            System.out.println("input concept = " + input.getModelReference());
            System.out.println("input doc = " + input.getName());
            printSimpleType(indent, input.getSimpletype());
            indent = "   ";
            List<ComplexTypeOBJ> temp = input.getComplextype();
            for (ComplexTypeOBJ complex : temp){
            	printComplexType(indent, complex);
            }

            
            indent = "";
            MessageOBJ output = op.getOutput();
            /*
            // get all leaf nodes of output
            for (SimpleTypeOBJ simple : WsOpReader.getAllSimpleType(output)){
            	System.out.println("simple name = " + simple.getName());
            	System.out.println("simple description = " + simple.getDescription());
            	System.out.println("simple type = " + simple.getType());
            	System.out.println("simple IRI = " + simple.getConcept());
            }
            */
            System.out.println("output = " + output.getName());
            printSimpleType(indent, output.getSimpletype());
            indent = "   ";
            temp = output.getComplextype();
            for (ComplexTypeOBJ complex : temp){
            	printComplexType(indent, complex);
            }
            System.out.println("=====================================================");
            
    	}
    	
    	// test annotation
    	OperationOBJ getParameters = semP.getAllOperations().get("getParameters");
    	semP.annotateWSDLOperaion(getParameters.getElement(), "modelReference", "http://123.com", sawsdlNS);
    	
    	// store to file
    	semP.updateToFile("D:/test123.sawsdl");
    	
    	// print xml
    	xml = semP.updateToXml();
    	//System.out.println("xml = \n" + xml);
    	
    	
    }

}
