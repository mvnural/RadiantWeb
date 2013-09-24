/*
 * Copyright (c) 2009 Srikalyan Swayampakula.. All rights reserved.
 * 
 *   Author : Srikalyan Swayampakula. .
 *   Name of the File : WADLParser.java .
 *   Created on : Nov 22, 2009 at 5:30:47 PM .
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
package edu.uga.cs.wstool.parser.sawadl;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import edu.uga.cs.wstool.parser.xml.XMLParser;

/**
 *
 * @author Srikalyan Swayampakula.
 */
public class WADLParser extends XMLParser
{

    private static Document currentDocument;
    private static Element rootElement;
    private static Namespace wadlNamespace;
    private Application application;
    private Map<String, Resource> resources;
    private static Namespace SAWADLNamespace = Namespace.getNamespace("sawadl", "http://cs.uga.edu/~guttula/sawadl.xsd");
    
    
    public WADLParser(URL fileURL)
    {
    	super(fileURL);
        try {
			currentDocument = super.getDoc();
			rootElement = currentDocument.getRootElement();
	        wadlNamespace = rootElement.getNamespace();
	        application = getApplication();
	        resources = getResources();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    } // constructor
    
	public WADLParser(String xml)
    {
    	super(xml);
        try {
			currentDocument = super.getDoc();
			rootElement = currentDocument.getRootElement();
	        wadlNamespace = rootElement.getNamespace();
	        application = getApplication();
	        resources = getResources();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    } // constructor
    
    public WADLParser(Document doc)
    {
    	super(doc);
        try {
			currentDocument = super.getDoc();
			rootElement = currentDocument.getRootElement();
	        wadlNamespace = rootElement.getNamespace();
	        application = getApplication();
	        resources = getResources();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    } // constructor
    
    public Namespace getNamespaceOfWADL()
    {
        return wadlNamespace;
    }
    
    public Application getApplicationOfWADL()
    {
        return application;
    }

    public Map<String, Resource> getResourcesMap()
    {
        return resources;
    }
    
    private Application getApplication() throws Exception
    {
        List<Doc> docs = getDocs(rootElement);
        List<Resources> resources = getResources(rootElement);
        Grammar grammar = getGrammar(rootElement);
        return new Application(resources, docs, grammar, null, rootElement);
    }
    
    /***
     * used to get docs list for an element eg. applicaiton.
     * @param element
     * @return list of document
     */
    private List<Doc> getDocs(Element e)
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = null;
        @SuppressWarnings("unchecked")
		List<Element> xmlDoc = e.getChildren(WADLConstant.DOC, wadlNamespace);
        if (xmlDoc != null)
        {
            docs = new ArrayList<Doc>();
            for (Element e1 : xmlDoc)
            {
                Doc tempDoc = getDoc(e1);
                if (tempDoc != null)
                {
                    docs.add(tempDoc);
                }
            }
        }
        return docs;
    }

    private Doc getDoc(Element e)
    {
        if (e == null)
        {
            return null;
        }
        String title = e.getAttributeValue(WADLConstant.DOC_TITLE);

        String lang = e.getAttributeValue(WADLConstant.DOC_LANG);
        if (lang == null)
        {
            @SuppressWarnings("unchecked")
			List<Attribute> temp = e.getAttributes();
            for (Attribute temp1 : temp)
            {
                if((temp1.getName()).equals(WADLConstant.DOC_LANG))
                    lang=temp1.getValue();
            }
        }
        String innerText = e.getText();
        return new Doc(title, lang, innerText);
    }

    private Grammar getGrammar(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = getDocs(e);
        List<Include> includes = getIncludes(e);
        List<Field> fields = getFields(e);
        return new Grammar(docs, includes, fields);
    }

	private List<Include> getIncludes(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Include> includes = null;
        @SuppressWarnings("unchecked")
		List<Element> xmlIncludes = e.getChildren(WADLConstant.INCLUDE, wadlNamespace);
        if (xmlIncludes != null)
        {
            includes = new ArrayList<Include>();
            for (Element xmlInclude : xmlIncludes)
            {
                Include tempInclude = getInclude(xmlInclude);
                if (tempInclude != null)
                {
                    includes.add(tempInclude);
                }
            }
        }
        return includes;
    }

    private Include getInclude(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = getDocs(e);
        URI href = null;
        String tempHref = e.getAttributeValue(WADLConstant.INCLUDE_HREF);
        if (tempHref != null)
        {
            href = new URI(tempHref);
        }
        return new Include(docs, href);
    }
    
    private List<Field> getFields(Element e) {
		
    	if (e == null)
        {
            return null;
        }
    	Element grammar = seekChildSingleNode(e, "grammar", true);
    	List<Element> fieldEles = seekMatchedChildrenNodes(grammar, "enumeration");
    	
    	List<Field> fields = new ArrayList<Field>();
    	for (Element ele : fieldEles){
    		String name = ele.getAttributeValue("value");
    		String modelRefernece = ele.getAttributeValue("modelRefernece");
    		fields.add(new Field(ele, name, modelRefernece));
    	}
    	return fields;
	
    }

    private List<Resources> getResources(Element e) throws Exception
    {
        if (e == null)
        {
        	return null;
        }

        List<Resources> resources = null;
        @SuppressWarnings("unchecked")
		List<Element> xmlResources = e.getChildren(WADLConstant.RESOURCES, wadlNamespace);
        if (xmlResources != null)
        {
            resources = new ArrayList<Resources>();
            for (Element tempResources : xmlResources)
            {
                Resources tempResource = getResourcesInstance(tempResources);
                if (tempResource != null)
                {
                    resources.add(tempResource);
                }
            }
        }

        return resources;

    }

    private Resources getResourcesInstance(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        String tempBase = e.getAttributeValue(WADLConstant.RESOURCES_BASE);
        URI base = null;
        if (tempBase != null)
        {
            base = new URI(tempBase);
        }
        List<Doc> docs = getDocs(e);
        List<Resource> subResources = getSubResources(e);
        return new Resources(docs, subResources, base, e);
    }

    private List<Resource> getSubResources(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Resource> subResources = null;
        @SuppressWarnings("unchecked")
		List<Element> xmlSubResources = e.getChildren(WADLConstant.RESOURCE, wadlNamespace);
        if (xmlSubResources != null)
        {
            subResources = new ArrayList<Resource>();
            for (Element xmlSubResource : xmlSubResources)
            {
                Resource subResource = getSubResource(xmlSubResource);
                if (subResource != null)
                {
                    subResources.add(subResource);
                }
            }
        }

        return subResources;

    }

    private Resource getSubResource(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = getDocs(e);
        List<Param> params = getParams(e);
        List<Method> methods = getMethods(e);
        List<Resource> resources = getSubResources(e);
        String id = e.getAttributeValue(WADLConstant.RESOURCE_ID);
        String queryType = e.getAttributeValue(WADLConstant.RESOURCE_QUERY_TYPE);
        String path = e.getAttributeValue(WADLConstant.RESOURCE_PATH);
        //System.out.println("the path is " + path + " wadlNampeSpace is " + wadlNamespace);
        return new Resource(docs, params, methods, resources, id, queryType, path, e);
    }

    private List<Param> getParams(Element e) throws Exception
    {
        if (e == null)
        {
            return null;

        }
        List<Param> params = null;
        @SuppressWarnings("unchecked")
		List<Element> xmlParams = e.getChildren(WADLConstant.PARAM, wadlNamespace);
        if (xmlParams != null)
        {
            params = new ArrayList<Param>();
            for (Element xmlParam : xmlParams)
            {
                Param param = getParam(xmlParam);
                if (param != null)
                {
                    params.add(param);

                }
            }
        }
        return params;
    }

    private Param getParam(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = getDocs(e);
        List<Option> options = getOptions(e);
        Link link = getLink(e.getChild(WADLConstant.LINK, wadlNamespace));
        String tempHref = e.getAttributeValue(WADLConstant.PARAM_HREF);//, wadlNamespace);
        URI href = null;
        if (tempHref != null)
        {
            href = new URI(tempHref);
        }
        String name = e.getAttributeValue(WADLConstant.PARAM_NAME);//, wadlNamespace);
        String style = e.getAttributeValue(WADLConstant.PARAM_STYLE);//, wadlNamespace);
        String id = e.getAttributeValue(WADLConstant.PARAM_ID);//, wadlNamespace);
        String type = e.getAttributeValue(WADLConstant.PARAM_TYPE);//, wadlNamespace);
        String default1 = e.getAttributeValue(WADLConstant.PARAM_DEFAULT);//, wadlNamespace);
        String tempRequired = e.getAttributeValue(WADLConstant.PARAM_REQUIRED);//, wadlNamespace);
        boolean required = false;
        if (tempRequired != null)
        {
            required = Boolean.valueOf(tempRequired);
        }
        //String tempRepeating = e.getAttributeValue(WADLConstant.PARAM_REPEATING);//, wadlNamespace);
        //boolean repeating = false;
        //if (tempRepeating != null)
        //{
        //    repeating = Boolean.valueOf(tempRepeating);
        //}
        String fixed = e.getAttributeValue(WADLConstant.PARAM_FIXED);//, wadlNamespace);
        String path = e.getAttributeValue(WADLConstant.PARAM_PATH);//, wadlNamespace);
        return new Param(docs, options, link, href, name, style, id, type, default1, fixed, path, required, e);
    }

    private List<Option> getOptions(Element e)
    {
        //System.out.println(e.getName());
    	if (e == null)
        {
            return null;
        }
        List<Option> options = null;
        @SuppressWarnings("unchecked")
		List<Element> xmlOptions = e.getChildren(WADLConstant.OPTION, wadlNamespace);
        if (xmlOptions != null)
        {
            options = new ArrayList<Option>();
            for (Element xmlOption : xmlOptions)
            {
                Option option = getOption(xmlOption);
                if (option != null)
                {
                    options.add(option);
                }
            }
        }
        return options;
    }

    private Option getOption(Element e)
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = getDocs(e);
        String value = e.getAttributeValue(WADLConstant.OPTION_VALUE);//, wadlNamespace);
        String mediaType = e.getAttributeValue(WADLConstant.OPTION_MEDIA_TYPE);//, wadlNamespace);
        return new Option(docs, value, mediaType);
    }

    private Link getLink(Element e)
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = getDocs(e);
        String resourceType = e.getAttributeValue(WADLConstant.LINK_RESOURCE_TYPE);//, wadlNamespace);
        String rel = e.getAttributeValue(WADLConstant.LINK_REL);//, wadlNamespace);
        String rev = e.getAttributeValue(WADLConstant.LINK_REV);//, wadlNamespace);
        return new Link(docs, resourceType, rel, rev);
    }

    private List<Method> getMethods(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Method> methods = null;
        @SuppressWarnings("unchecked")
		List<Element> xmlMethods = e.getChildren(WADLConstant.METHOD, wadlNamespace);
        if (xmlMethods != null)
        {
            methods = new ArrayList<Method>();
            for (Element xmlMethod : xmlMethods)
            {
                Method method = getMethod(xmlMethod);
                if (method != null)
                {
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    private Method getMethod(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = getDocs(e);
        Request request = getRequest(e.getChild(WADLConstant.REQUEST, wadlNamespace));
        List<Response> responses = getResponses(e);
        String id = e.getAttributeValue(WADLConstant.METHOD_ID);//, wadlNamespace);
        String tempHref = e.getAttributeValue(WADLConstant.METHOD_HREF);//, wadlNamespace);
        URI href = null;
        if (tempHref != null)
        {
            href = new URI(tempHref);
        }
        String name = e.getAttributeValue(WADLConstant.METHOD_NAME);//, wadlNamespace);
        return new Method(docs, request, responses, id, name, href, e);
    }

    private Request getRequest(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = getDocs(e);
        List<Param> params = getParams(e);
        List<Representation> representations = getRepresentations(e);
        return new Request(docs, params, representations);
    }

    private List<Representation> getRepresentations(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Representation> representations = null;
        @SuppressWarnings("unchecked")
		List<Element> xmlRepresentations = e.getChildren(WADLConstant.REPRESENTATION, wadlNamespace);
        if (xmlRepresentations != null)
        {
            representations = new ArrayList<Representation>();
            for (@SuppressWarnings("unused") Element xmlRepresentation : xmlRepresentations)
            {
                Representation representation = getRepresentation(e);
                if (representation != null)
                {
                    representations.add(representation);
                }
            }
        }
        return representations;
    }

    private Representation getRepresentation(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = getDocs(e);
        List<Param> params = getParams(e);
        String id = e.getAttributeValue(WADLConstant.REPRESENTATION_ID);//, wadlNamespace);
        String element = e.getAttributeValue(WADLConstant.REPRESENTATION_ELEMENT);//, wadlNamespace);
        String mediaType = e.getAttributeValue(WADLConstant.REPRESENTATION_MEDIA_TYPE);//, wadlNamespace);
        String tempHref = e.getAttributeValue(WADLConstant.REPRESENTATION_HREF);//, wadlNamespace);
        URI href = null;
        if (tempHref != null)
        {
            href = new URI(tempHref);
        }
        String profile = e.getAttributeValue(WADLConstant.REPRESENTATION_PROFILE);//, wadlNamespace);
        return new Representation(docs, params, id, element, mediaType, href, profile);
    }

    private List<Response> getResponses(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Response> responses = null;
        @SuppressWarnings("unchecked")
		List<Element> xmlResponses = e.getChildren(WADLConstant.RESPONSE, wadlNamespace);
        if (xmlResponses != null)
        {
            responses = new ArrayList<Response>();
            for (@SuppressWarnings("unused") Element xmlResponse : xmlResponses)
            {
                Response response = getResponse(e);
                if (response != null)
                {
                    responses.add(response);
                }
            }
        }
        return responses;
    }

    private Response getResponse(Element e) throws Exception
    {
        if (e == null)
        {
            return null;
        }
        List<Doc> docs = getDocs(e);
        List<Param> params = getParams(e);
        List<Representation> representations = getRepresentations(e);
        String tempStatus = e.getAttributeValue(WADLConstant.RESPONSE_STATUS);//, wadlNamespace);
        int status = -1;
        if (tempStatus != null)
        {
            status = Integer.parseInt(tempStatus);
        }
        return new Response(docs, params, representations, status);

    }

    public Document getCurrentDoc(){
    	return currentDocument;
    }
    
	public Map<String, Element> getParams(){
		
		HashMap<String, Element> result = new HashMap<String, Element>();
		
		for (Resource re : resources.values()){
			result.putAll(getParams(re));
		}
		
		return result;	
	}
	
	public Map<String, Element> getParams(Resource re){
		
		HashMap<String, Element> result = new HashMap<String, Element>();
		
		// inside resource
		for (Param param : re.getParams()){
			result.put(param.getName(), param.getElement());
		}
		
		// inside method
		for (Method method : re.getMethods()){
			// inside request
			if (method.getRequest() != null){
				for (Param param : method.getRequest().getParams()){
					result.put(param.getName(), param.getElement());
				}
				// inside request representation
				for (Representation representation : method.getRequest().getRepresentations()){
					for (Param param : representation.getParams()){
						result.put(param.getName(), param.getElement());
					}
				}
			}
		}
		
		return result;
		
	}
	
	public Map<String, Resource> getResources(){
		
		HashMap<String, Resource> result = new HashMap<String, Resource>();
		
		for (Resources res : application.getResources()){
			for (Resource re : res.getResources()){
				result.putAll(getResources(re));
			}
		}
		
		return result;	
	}
	
	public Map<String, Resource> getResources(Resource re){
		
		HashMap<String, Resource> result = new HashMap<String, Resource>();

		if (re.getPath() != null) result.put(re.getPath(), re);
		
		// search nested resource
		if (re.getResources().size() != 0) {
			for (Resource re1 : re.getResources()) {
				result.putAll(getResources(re1));
			}
		}
		
		return result;
		
	}

	public Element getMethod(String MethodID) {
		
		Element method = null;
    	// get the parameter by its name
    	// search the mothod inside the resource
    	for (String key : resources.keySet()){
    		for (Method m : resources.get(key).getMethods()){
    			if ((m.getId() != null) && (m.getId().equals(MethodID))) {
    				method = m.getElement();
    				break;
    			}
    		}
    		if (method != null) break;
    	}
    	
    	return method;
		
	}

	public String annotateWADLResource(String resourcePath, String type, String value){
		// check the resource whose path is equal to input path
		Map<String, Resource> resources = getResourcesMap();
        Element resEle = resources.get(resourcePath).getElement(); 
        String messg = "";
    	// check if there is a path, if not, it has an error
    	if((resourcePath != null))
        {
    		// if there is no element, the return error
            if (resEle == null){
            	messg = "Error: Resource of path: " + resourcePath + " not found";
            	return messg;
            }else{
            	messg = addAnnotation(resEle, type, value, SAWADLNamespace);
            }
        }
        else{
            messg ="Error: Resource of path: " + resourcePath + " not found";
        }   
        return messg;
    }

	public String annotateWADLParam(String paraName, String type, String value){
        // get the parameter by its name
    	Element param = getParams().get(paraName);
        String messg = "";
        // check if there is a param
        if(param != null){
        	messg = addAnnotation(param, type, value, SAWADLNamespace);
        }else{
            messg = "Error: Param " + paraName + " not found";
        }
        return messg;
    }
    
    public String annotateWADLMethod(String id, String type, String value){
        
    	Element method = null;
    	// get the parameter by its name
    	Map<String, Resource> resources = getResources();
    	// search the mothod inside the resource
    	for (String key : resources.keySet()){
    		for (Method m : resources.get(key).getMethods()){
    			
    			
    			System.out.println("m.id = " + m.getId());
    			System.out.println("id = " + id);
    			
    			if ((m.getId() != null) && (m.getId().equals(id))) {
    				method = m.getElement();
    				break;
    			}
    		}
    		if (method != null) break;
    	}
        String messg = "";
        // check if there is a param
        if(method != null){
            messg = addAnnotation(method, type, value, SAWADLNamespace);
        }else{
            messg = "Error: method " + id + " not found";
        }
        return messg;
    }
    
    public static Namespace getSAWADLNamespace(){
    	return SAWADLNamespace;
    }
    
    public String removeSAWADLAnnotation(Element ele, String attr, String value){
		String msg = removeAnnotation(ele, attr, value, SAWADLNamespace);
		return msg;
	}
    
    /**
     * for test
     */
    public static void main(String[] args) throws Exception {
    	
    	// access by file system
    	//URL fileURL = new URL("file:/D:/GeneByLocusTag.wadl");
    	//WADLParser wadl = new WADLParser(fileURL);
    	
    
    
    }
    
}
