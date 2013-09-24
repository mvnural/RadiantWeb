package edu.uga.cs.wstool.parser.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class XMLParser {

	/** the document for xml
     */
	private Document currentDocument;
	
    public XMLParser(URL fileURL)
    {
        try {
			currentDocument = generateDocumentation(fileURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    } // constructor
    
    public XMLParser(String xml)
    {
    	try {
        	ByteArrayInputStream byteIn = new ByteArrayInputStream(xml.getBytes("utf-8"));
        	currentDocument = generateDocumentation(byteIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
    } // constructor
    
    public XMLParser(Document doc)
    {
        currentDocument = doc;
    } // constructor
	
    /***
     * This method is used to return current document.
     * @return Document
     */
    public Document getDoc(){
    	return currentDocument;
    }
    
    /***
     * This method is used to generate the require document for jdom.
     * @param URL: file URL
     * @return Document
     * @throws Exception
     */
    public static Document generateDocumentation(URL fileURL) throws Exception
    {
        Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        doc = builder.build(fileURL);
        return doc;
    }
    
    /***
     * This method is used to generate the require document for jdom.
     * @param InputStream: input stream ex. FileInputStream or ByteArrayInputStream
     * @return Document
     * @throws Exception
     */
    public static Document generateDocumentation(InputStream in) throws JDOMException, IOException {
		Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        doc = builder.build(in);
		return doc;
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
    
    /**
     * get the first sub element by local name and attribute name
     * @param Element: parent element
     * @param String: eleName
     * @param boolean: if true, eleName is local name ,ex portType, binding.
     * @return Element: target element
     */
    public static Element seekChildSingleNode(Element Ele, String elemName, boolean isLocalName) {
		Element subelem = null;
		for (Object e : Ele.getChildren()){
			if (isLocalName == true){
				if (((Element)e).getName().equals(elemName)){
					subelem = (Element)e;
					return subelem;
				}
			}else{
				if (((Element)e).getAttributeValue("name") != null){
					if (((Element)e).getAttributeValue("name").equals(elemName)){
						subelem = (Element)e;
						return subelem;
					}
				}
			}
		}
		return null;
	}
    
    /**
     *
     * @param elem The parent element
     * @param localName The target elements' name which we are searching
     * @return the list of target elements
     */
    public static List<Element> seekMatchedChildrenNodes(Element elem, String localName) {
    	
    	if (elem == null){
    		return new ArrayList<Element>();
    	}
    	List<Element> subelems = new ArrayList<Element>();
    	@SuppressWarnings("unchecked")
		List<Element> temp = elem.getChildren();
    	while(temp.size() != 0){
    		List<Element> nextLevel = new ArrayList<Element>();
    		for (Object e : temp){
    			if (((Element)e).getChildren().size() > 0){
    				for (Object child : ((Element)e).getChildren()){
    					nextLevel.add((Element)child);
    				}
    			}
    			if (((Element)e).getName().equals(localName)){
    				subelems.add((Element)e);
    			}
    		}
    		temp = nextLevel;
    	}
    	return subelems;
    }
    
    /**
     * return document of element which is referred to a simple type or complex type
     * @param Elememt: simple type element or complex type element
     * @return String: the document
     * @throws JDOMException 
     */
	public static String getAnnotation(Element e){
		Element annot = e.getChild("annotation", e.getNamespace());
		String annotation = "";
        if(annot != null){
            Element doc = annot.getChild("documentation", e.getNamespace());
            annotation = doc.getTextTrim();
        }
		return annotation;
	}
    
	/**
     * annotates with new value, if there exist a value, then new value will be appended. in final, return message
     * @param Elememt: jdom element for annotation
     * @param String: attribute name ex. modelReference
     * @param String: attribute value for annotation
     * @param Namespace: name space, ex sawsdl name space
     * @return String: message
     * @throws JDOMException 
     */
    public static String addAnnotation(Element ele, String attribute, String value, Namespace ns){
    	
    	String msg = "";
    	Attribute attr;
    	if (ele != null){
    		attr = ele.getAttribute(attribute, ns);
            String attrValue = null;
            boolean alreadyPresent = false;
            if(attr != null && attr.getValue() != null && !attr.getValue().isEmpty())
            {
                String [] values = attr.getValue().split(" ");
                for(String s : values){
                    if(s.trim().equalsIgnoreCase(value))
                    {
                        alreadyPresent = true;
                        break;
                    }
                }
                if(!alreadyPresent){
                	// append the new value
                    attrValue = attr.getValue()+" "+value;
                }else{
                	msg = "Error: annotation already present";
                }
            }else{
                attrValue = value;
            }
            if(!alreadyPresent){
            	Element e = ele.setAttribute(attribute, attrValue, ns);
                if(e != null){
                	msg = "Success: Annotated the element";
                }else{
                	msg = "Error: Failed to annotate the elemet";
                }
            }
    	}else{
    		msg = "Error: element is null";
    	}
		return msg;
    }
    
    /**
     * remove new value, if there exist a value, only new value will be removed. in final, return message
     * @param Elememt: jdom element for annotation
     * @param String: attribute name ex. modelReference
     * @param String: attribute value for removing
     * @param Namespace: name space, ex sawsdl name space
     * @return String: message
     * @throws JDOMException 
     */
    public static String removeAnnotation(Element ele, String attribute, String value, Namespace ns){
        
    	String msg = "";
    	Attribute attr;
    	if (ele != null){
    		attr = ele.getAttribute(attribute, ns);
    		String[] values = attr.getValue().split(" ");
    		String newValue = "";
    		boolean found = false;
    		for (String val : values) {
    			if (val.equalsIgnoreCase(value)) {
    				// skip the value
    				found = true;
    			} else {
    				// reconstruct the new value without the specific value
    				newValue += (" " + val);
    			}
    		}
    		newValue = newValue.trim();
    		if (found) {
    			if (newValue != null) {
    				// msg = "Success";
    				if (newValue.trim().length() != 0) {
    					Element e = ele.setAttribute(attribute, newValue, ns);
    					if (e == null){
    						msg = "Error: Failed to remve annotation";
    					}else{
    						msg = "Success: Annotation removed";
    					}
    				} else {
    					boolean removed = ele.removeAttribute(attr);
    					if (removed){
    						msg = "Success: Annotation removed";
    					}else{
    						msg = "Error: Failed to remve annotation";
    					}
    				}
    			}
    		} else {
    			msg = "Error: No annotation found";
    		}
    	}else{
    		msg = "Error: element is null";
    	}
		return msg;
    }
    
    /**
     * return the current state to file 
     * @param String: file path and name
     * @throws JDOMException 
     */
    public void updateToFile(String savefile){
        
    	OutputStream out = null;
        try {
            XMLOutputter xmlout = new XMLOutputter();
            File file1 = new File(savefile);
            out = new FileOutputStream(file1);
            xmlout.output(currentDocument, out);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * return the current state to xml String 
     * @return String: xml
     * @throws JDOMException 
     */
    public String updateToXml(){
        
    	String xml = "";
    	ByteArrayOutputStream out;
        try {
            XMLOutputter xmlout = new XMLOutputter();
            out = new ByteArrayOutputStream();
            //Document doc1 = Serializer.deserializeDoc(file);
            //xmlout.output(doc1, out);
            xmlout.output(currentDocument, out);
            xml = new String (out.toByteArray(), "utf-8");
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
        return xml;
    }
    
    /**
     * return the current state to xml String
     * @param Document: the document
     * @return String: xml
     * @throws JDOMException 
     */
    public static byte[] updateToXml(Document doc){
        
    	byte[] result = null;
    	ByteArrayOutputStream out;
        try {
            XMLOutputter xmlout = new XMLOutputter();
            out = new ByteArrayOutputStream();
            xmlout.output(doc, out);
            result = out.toByteArray();
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
	
}
