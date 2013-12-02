package edu.uga.radiant.ajax;

import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.wstool.parser.sawadl.WADLParser;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.xml.XMLParser;
import edu.uga.radiant.printTree.LoadWADLTree;
import edu.uga.radiant.printTree.LoadWSDLTree;
import edu.uga.radiant.util.QueryManager;
import edu.uga.radiant.util.RadiantToolConfig;

public class LoadWS extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errormsg;
	private String wsloc;
	private String type;
	private String innerTreeHtml;
	private File WSFile;
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
	    Logger logger = RadiantToolConfig.getLogger();
		String sawsdlExt = ".sawsdl";
		String wsdlExt = ".wsdl";
		String sawadlExt = ".sawadl";
		String wadlExt = ".wadl";
		StringBuffer buf = new StringBuffer();

		SAXBuilder sbuilder = new SAXBuilder();
        Document doc = null;
        
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		String importURL = "";
        errormsg = "";
        String filename = "";
        
        logger.debug("wsloc = " + wsloc);
		if (WSFile != null) logger.debug("WSFile size = " + WSFile.getTotalSpace());
		
		try {
			XMLParser wsParser = null;
			session.remove("wsname");
			session.remove("wsdlparser");
			session.remove("wadlparser");
			//If it is a remote file
			if (wsloc.indexOf("http:") != -1){	
	        	importURL = wsloc;
        		doc = sbuilder.build(importURL);
	        	if (isWSDL(doc)){
					wsParser = new SAWSDLParser(doc);
		        	session.put("wsdlparser", wsParser);
		        	session.remove("wadlparser");
		        	
	        	}else if (isWADL(doc)){
					wsParser = new WADLParser(doc);
		        	session.put("wadlparser", wsParser);
		        	session.remove("wsdlparser");

	        	}
	        	int start = importURL.lastIndexOf("/");
	        	filename = importURL.substring(start, importURL.length());
	        	filename = QueryManager.getUniqueFileName(filename);
	        	session.put("wsname", filename);
        	// The document is being fetched from the database
			}else if(wsloc.startsWith("db:")){
				filename = wsloc.substring(3); 
				String documentXml = QueryManager.getServiceXml(filename);
				doc = sbuilder.build(new StringReader(documentXml));
				if(isWSDL(doc)){
					wsParser = new SAWSDLParser(doc);
	    			session.put("wsdlparser", wsParser);
				}else{
					wsParser = new WADLParser(doc);
    	        	session.put("wadlparser", wsParser);
				}
    			
	        	session.put("wsname", filename);
				
			// Document is being uploaded from computer
			}else{
	        	if (WSFile != null){
	        		filename = QueryManager.getUniqueFileName(wsloc);
	        		doc = sbuilder.build(WSFile);
	        		if (isWSDL(doc)){
	        			wsParser = new SAWSDLParser(doc);
	    	        	session.put("wsdlparser", wsParser);
	    	        	session.put("wsname", filename);
	    		    }else if (isWADL(doc)){
	        			wsParser = new WADLParser(doc);
	    	        	session.put("wadlparser", wsParser);
	    	        	session.put("wsname", filename);
	    		    }else{
	        			errormsg = "File is not wsdl or wadl file.";
	        		}
	        	}else{
	        		errormsg = "WSDL file lost.";
	        	}
	        }
			
			if (wsParser == null){
				errormsg = "Web service document is invalid";
				return ERROR;
			}
			
			if(isWSDL(doc)){
	            
				boolean hasSAWSDLNS = false;
	            @SuppressWarnings("rawtypes")
				List nameSpaces = doc.getRootElement().getAdditionalNamespaces();
	            for(int i = 0 ; i < nameSpaces.size(); i++){
	                Namespace ns = (Namespace)nameSpaces.get(i);
	                if(ns != null && ns.getURI() != null && SAWSDLParser.sawsdlNS != null && SAWSDLParser.sawsdlNS.getURI() != null && ns.getURI().equalsIgnoreCase(SAWSDLParser.sawsdlNS.getURI())){
	                    hasSAWSDLNS = true;
	                    break;
	                }
	            }
	            if(!hasSAWSDLNS){
	                if (SAWSDLParser.sawsdlNS != null) doc.getRootElement().addNamespaceDeclaration(SAWSDLParser.sawsdlNS);
	            }
	        
	            boolean wsdlV1 = ((SAWSDLParser) wsParser).isWsdlV1();
	            if(wsdlV1 == true){
	            	LoadWSDLTree.loadWSDL((SAWSDLParser) wsParser, buf, filename);   
	            }else{
	            	// not implemented yet
	            }
	            innerTreeHtml = buf.toString();
	            type = "wsdl";
	        }
	        else if(isWADL(doc)){
	        	// not implemented yet
	        	LoadWADLTree.loadWADL((WADLParser) wsParser, buf, wsloc);
	        	innerTreeHtml = buf.toString();
	        	type = "wadl";
	        }

		} catch (Exception e) {
			e.printStackTrace();
			errormsg = e.toString();
			// record error log
			logger.error(e.toString());
		}
		
		return SUCCESS;
	}
	
	public static boolean isWADL(Document doc){
        boolean iswadl = false;
        Element root = doc.getRootElement();
        String rootTag = root.getName();
        if(rootTag.equalsIgnoreCase("application"))
            iswadl = true;
        return iswadl;
    }
	
	public static boolean isWSDL(Document doc){
        boolean iswsdl = false;
        Element root = doc.getRootElement();
        String rootTag = root.getName();
        if(rootTag.equalsIgnoreCase("description") || rootTag.equalsIgnoreCase("definitions"))
            iswsdl = true;
        return iswsdl;
    }

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setWSFile(File wSFile) {
		WSFile = wSFile;
	}

	public void setWsloc(String wsloc) {
		this.wsloc = wsloc;
	}

	public String getWsloc() {
		return wsloc;
	}

	public void setInnerTreeHtml(String innerTreeHtml) {
		this.innerTreeHtml = innerTreeHtml;
	}

	public String getInnerTreeHtml() {
		return innerTreeHtml;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	// to prevent the file is inside reply message
	//public File getWSFile() {
	//	return WSFile;
	//}

}
