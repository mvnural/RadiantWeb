package edu.uga.cs.lumina.discovery.action;

import java.net.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.io.*;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.lumina.discovery.util.URLlink;
import edu.uga.cs.lumina.discovery.util.WSDLSystem;
import edu.uga.radiant.util.DataBaseConnection;
import edu.uga.radiant.util.QueryManager;
import edu.uga.radiant.util.RadiantToolConfig;

/**
 * 
 * @author Long
 *
 * This class is used to search the biocatlague website's wsdl or sawsdl file
 */
public class SearchWSDL extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * wsdl link information, the wsdl name is key, and link information is value
	 */
	private HashMap<String, URLlink> WSDLs;
	
	/**
	 * The database connection
	 */
	private DataBaseConnection dbcn = new DataBaseConnection();
	
	/**
	 * sql connection
	 */
	private Connection connection = dbcn.getConnection();
	
	/**
	 * the search keyword from input text tag value
	 */
	private String keyword;
	
	/**
	 * the total wsdl number in database
	 */
	private int wsdlNum;
	
	/**
	 * the total sawsdl number in database
	 */
	private int sawsdlNum;
	
	/**
	 * the collection of wsdl information
	 */
	private ArrayList<URLlink> wsdls;
	
	/**
	 * the collection of sawsdl information
	 */
	private ArrayList<URLlink> sawsdls;
	
	/**
	 * error message
	 */
	private String errormsg;
	
	/**
	 * the page number
	 */
	private String pageid;
	
	/**
	 * the total page number
	 */
	private int totalPages;
	
	/**
	 * the start page number
	 */
	private int startPage;

	/**
	 * The error message
	 */
	private String errorMesg = "";
	
	/**
	 * The collection of login error message
	 */
	private Vector<String> loginError;
	
	/**
	 * indicate the message type, ex. success or error
	 */
	private String messageType = "";
	
	/**
	 * the struts2 action
	 */
	public String execute() {
		
		wsdlNum = 0;
		sawsdlNum = 0;
		WSDLs = new HashMap<String, URLlink>();
		SAXBuilder builder = new SAXBuilder();
		errormsg = "";
		if (pageid == null) pageid = "";
		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		
		if (session.get("login") != "true"){
			session.remove("login");
			session.remove("userID");
			session.remove("accountType");
			errorMesg = "Please login first! if you don't have account, apply a new one. ";
			loginError.add(errorMesg);
			messageType = "error";
			dbcn.close();
			return LOGIN;
		}
		
		try {
			
			String baseURL = "http://www.biocatalogue.org/services.xml?t=[SOAP]";
			String querykeyword = "";
			
			if (keyword != null && !keyword.equals("")){
				querykeyword = "&q=" + URLEncoder.encode(keyword, "utf8") + "&page=1";
			}else{
				errormsg = "keyword can not be empty!";
				return ERROR;
			}
			
			System.out.println("encode querykeyword = " + querykeyword);
			
			URL wsURL = new URL(baseURL + querykeyword);
	        URLConnection ws = wsURL.openConnection();
	        WSDLSystem wsdlSYS = new WSDLSystem(connection);
			
			Document document = (Document) builder.build(new InputStreamReader(ws.getInputStream()));
			Element rootNode = document.getRootElement();
			Namespace xlink = rootNode.getNamespace("xlink");
			HashMap<String, String> services = new HashMap<String, String>();
			
			int pages = 0;
			Element statistics = getSubElement(rootNode, "statistics");
			@SuppressWarnings("rawtypes")
			List list = statistics.getChildren();
			for (int i = 0; i < list.size(); i++) {
				Element node = (Element) list.get(i);
				if (node.getName().equals("pages")) totalPages = Integer.parseInt(node.getValue());	
			}
			errormsg = "total pages is " + totalPages + " ,";
			
			
			System.out.println("pageid = " + pageid);
			// split pages to display
			startPage = 1;
			int splitpages = 1;
			if ((totalPages > (1 + splitpages - 1)) && pageid.equals("")) {
				
				pages = 1;
				
			}else if (totalPages == (1 + splitpages - 1)){
			
				pages = totalPages;
			
			}else if (!pageid.equals("")){
			
				startPage = Integer.valueOf(pageid);
				if (totalPages >= (startPage + splitpages - 1)){
					pages = startPage + splitpages - 1;
				}
				
			}
			errormsg = errormsg + "show page " + startPage;
			
			
			System.out.println("startPage = " + startPage);
			System.out.println("pages = " + pages);
			System.out.println("errormsg = " + errormsg);
			
			for (int i = startPage; i <= pages; i++){
				
				if (i != 1){
					querykeyword = "&q=" + URLEncoder.encode(keyword, "utf8") + "&page=" + i;
					
					System.out.println("new page encode querykeyword = " + querykeyword);
					
					wsURL = new URL(baseURL + querykeyword);
					ws = wsURL.openConnection();
					document = (Document) builder.build(new InputStreamReader(ws.getInputStream()));
					rootNode = document.getRootElement();
					xlink = rootNode.getNamespace("xlink");
				}
				
				System.out.println("searching the page " + i + " SOAP service, total pages is " + pages + "....");
				
				Element results  = getSubElement(rootNode, "results");
				@SuppressWarnings("rawtypes")
				List list1 = results.getChildren();
				for (int j = 0; j < list1.size(); j++) {
					Element service = (Element) list1.get(j);
					//System.out.println(getStatus(service));
					if (getStatus(service).equals("PASSED")){
						services.put(service.getAttributeValue("resourceName"), service.getAttributeValue("href", xlink) + ".xml");
						//System.out.println("service name = " + service.getAttributeValue("resourceName")); 
					}  
				}

				for (String name : services.keySet()){
					
					URL temp = new URL(services.get(name));
					//System.out.println("temp = " + temp);
					URLConnection conn = temp.openConnection();
					Document doc = (Document) builder.build(new InputStreamReader(conn.getInputStream()));
					Element root = doc.getRootElement();
					Element variant = getSubElement(root, "variants");
					Element soap = getSubElement(variant, "soapService");
					
					URLlink link = new URLlink();
					link.setName(name);
					String wsLocation = getSubElementText(soap, "wsdlLocation");
					link.setWsLocation(wsLocation);
					String wsBackupLocation = soap.getAttribute("href", xlink).getValue() + "/latest_wsdl";
					link.setBackupWSDLURL(wsBackupLocation);
					// search the SAWSDL
					String sawsLocation = getSubElementSAWASDLlink(soap, "documentationUrl");
					link.setSAWSDLURL(sawsLocation);
					
					// set atomic 
					connection.setAutoCommit(false);
					
					// save file
					String wSDLmessage = saveWSDL(wsdlSYS, link);
					link.setWSDLmessage(wSDLmessage);
					
					String sAWSDLmessage = saveSAWSDL(wsdlSYS, link);
					link.setSAWSDLmessage(sAWSDLmessage);
					
					connection.commit();
					
					WSDLs.put(name, link);
				
				}
			
			}
			
			wsdls = searchWSDL(WSDLs);
			sawsdls = searchSAWSDL(WSDLs);
			
			wsdlNum = QueryManager.getWSDLList(connection).size();
			sawsdlNum = QueryManager.getWSDLList(connection).size();
			
		}catch (Exception e) {
			e.printStackTrace();
			
			// record error log
			Logger logger = RadiantToolConfig.getLogger();
			logger.error(e.toString());
			
			return ERROR;
		}
			
			return SUCCESS;
	}

	/**
	 * get the collection of link information of wsdl
	 * @param results the collection of link information of wsdl
	 * @return
	 */
	public ArrayList<URLlink> searchWSDL(HashMap<String, URLlink> results){
		ArrayList<URLlink> wsdls = new ArrayList<URLlink>();
		for (String name : results.keySet()){
    		URLlink link = results.get(name);
    		wsdls.add(link);
		}
		return wsdls;
	}
	
	/**
	 * get the collection of link information which has sawsdl url
	 * @param results the collection of link information of sawsdl
	 * @return
	 */
	public ArrayList<URLlink> searchSAWSDL(HashMap<String, URLlink> results){
		ArrayList<URLlink> SAWSDLs = new ArrayList<URLlink>();
		for (String name : results.keySet()){
    		URLlink link = results.get(name);
    		if(!(link.getSAWSDLURL().equals(""))) {
    			SAWSDLs.add(link);
    		}
		}
		return SAWSDLs;
	}
	
	/**
	 * save the wsdl to database
	 * @param sys The WSDL system which manage the database and save wsdl by url
	 * @param link the wsdl information
	 * @return the success or error message 
	 */
	public String saveWSDL(WSDLSystem sys, URLlink link){
		//String message = sys.saveWSDL(new URL(link.getWsLocation()), link.getName(), "wsdl");
    	//if (!message.equals("")){
    	//	return message;
    	//}
		return "";
	}
	
	/**
	 * save the sawsdl to database
	 * @param sys The WSDL system which manage the database and save sawsdl by url
	 * @param link the sawsdl information
	 * @return the success or error message 
	 */
	public String saveSAWSDL(WSDLSystem sys, URLlink link){
		//if(!(link.getSAWSDLURL().equals(""))) {
    	//	String message = sys.saveSAWSDL(link);
    	//	if (!message.equals("")){  			
    	//		return message;
    	//	}
    	//}
		return "";
	}
	
	/**
	 * get first sub jdom elements for parent element 
	 * @param root the parent jdom element
	 * @param name the sub element name
	 * @return the matching sub jdom element
	 */
    public static Element getSubElement(Element root, String name){
    	Element result = null;
		@SuppressWarnings("rawtypes")
		List list = root.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Element node = (Element) list.get(i);
			if (node.getName().equals(name)){
				result = node;
				break;
			}
		}
    	return result;
    	
    }
    
    /**
     * get first sub jdom element text for parent element 
     * @param root the parent element
     * @param name the sub element name
     * @return the inside text
     */
    public static String getSubElementText(Element root, String name){
    	String result = "";
    	if (root == null) return result;
		@SuppressWarnings("rawtypes")
		List list = root.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Element node = (Element) list.get(i);
			if (node.getName().equals(name)){
				result = node.getText();
				break;
			}
		}
    	return result;
    }
    
    /**
     * parse the xml file from biocatlogue which identify the status of this wsdl information
     * @param root the root element of biocatlogue xml file
     * @return the status of this wsdl
     */
    public static String getStatus(Element root){
    	String result = null;
		@SuppressWarnings("rawtypes")
		List list = root.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Element node = (Element) list.get(i);
			if (node.getName().equals("latestMonitoringStatus")){
				result = getSubElementText(node, "label");
				break;
			}
		}
    	return result;
    }
    
    /**
     * parse the xml file from biocatlogue and get we put the sawsdl link url of this wsdl information
     * @param root the root element of biocatlogue xml file 
     * @param name the searching wsdl name
     * @return the url of sawsdl link
     */
    public static String getSubElementSAWASDLlink(Element root, String name){
    	String result = "";
		@SuppressWarnings("rawtypes")
		List list = root.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Element node = (Element) list.get(i);
			if (node.getName().equals(name)){
				String text = node.getText();
				if (text.contains("SAWSDL: http://")){
					return text.substring(text.indexOf("http://"), text.length());
				}
			}
		}
    	return result;
    	
    }

    public HashMap<String, URLlink> getWSDLs(){		
		return WSDLs;
	}
    
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}
	
	public void setWsdlNum(int wsdlNum) {
		this.wsdlNum = wsdlNum;
	}

	public int getWsdlNum() {
		return wsdlNum;
	}

	public void setSawsdlNum(int sawsdlNum) {
		this.sawsdlNum = sawsdlNum;
	}

	public int getSawsdlNum() {
		return sawsdlNum;
	}

	public void setWsdls(ArrayList<URLlink> wsdls) {
		this.wsdls = wsdls;
	}

	public ArrayList<URLlink> getWsdls() {
		return wsdls;
	}

	public void setSawsdls(ArrayList<URLlink> sawsdls) {
		this.sawsdls = sawsdls;
	}

	public ArrayList<URLlink> getSawsdls() {
		return sawsdls;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setPageid(String pageid) {
		this.pageid = pageid;
	}

	public String getPageid() {
		return pageid;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setLoginError(Vector<String> loginError) {
		this.loginError = loginError;
	}

	public Vector<String> getLoginError() {
		return loginError;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageType() {
		return messageType;
	}


}