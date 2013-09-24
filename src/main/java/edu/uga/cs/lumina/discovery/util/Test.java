package edu.uga.cs.lumina.discovery.util;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class Test {
	
	
	private static Namespace xsdNS = null; 
	private static Namespace wsdlNS = null;    
	public static Namespace sawsdlNS = null;
	
	public static Element getSchemaElem(String fileName)
    {
        String fileUrl = fileName;//ClassLoader.getSystemResource(fileName).toString();

        SAXBuilder sbuilder = new SAXBuilder();
        Element schemaEle = null;

        try
        {
            Document doc = sbuilder.build(fileUrl);

            //get root element of xml file
            Element root = doc.getRootElement();

            //defind namespace for requested elements
            //Namespace xsdNS = root.getNamespace("xsd");
            //xsdNS = Namespace.getNamespace("http://www.w3.org/2001/XMLSchema");
            xsdNS = root.getNamespace("xsd");
            wsdlNS = root.getNamespace("wsdl");
            sawsdlNS = root.getNamespace("sawsdl");
            
            System.out.println("xsdNS = " + xsdNS);
            System.out.println("wsdlNS = " + wsdlNS);
            System.out.println("sawsdlNS = " + sawsdlNS);
            
            
            schemaEle = root.getChild("types", wsdlNS).getChild("schema", xsdNS);
            String preXsdNs = schemaEle.getNamespacePrefix();
            xsdNS = Namespace.getNamespace(preXsdNs, "http://www.w3.org/2001/XMLSchema");
            //System.out.println("prefix--" + preXsdNs);

        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return schemaEle;
    }


	public static void main(String args[]) throws Exception {
		
		//List<WsOpReader> adv = null;
		
		//String filename = "clustalw2.sawsdl";
		//String temp = "wublast.wsdl";
		//String wsdlURL = "file:/D:/" + filename;
		
		//File test = new File(new URI("file:/D:/12121webService.owl"));
		//System.out.println(test.getTotalSpace());
		
		
		//OntologyManager discoveryMgr = new OntologyManager("D:/webService.owl");
		//OWLClass thing = discoveryMgr.getConceptClassByLabel("Thing");
		//discoveryMgr.getThing();
		//System.out.println("thing = " + thing);
		//System.out.println("thing label = " + discoveryMgr.getClassLabel(thing));
		
		//adv = WsOpReader.readWSDL(discoveryMgr, wsdlURL);
        
		
        
        
		/*
		Element schemaEle = getSchemaElem(wsdlURL);
        String preXsdNs = schemaEle.getNamespacePrefix();
        List<String> subEleNameList = new ArrayList<String>();
        List<String> subEleTypeList = new ArrayList<String>();
        List<String> subEleMRList = new ArrayList<String>();
        List<String> subEleXpathList = new ArrayList<String>();
        
        String eleName = "getSupportedDBs";  
        subEleNameList.add(eleName);    
        subEleTypeList.add(eleName);    
        subEleMRList.add(eleName);    
        subEleXpathList.add("");    
        int subEleLiSize = subEleNameList.size();
            
        // get the first element to start loop searching 
        String elemName = subEleNameList.get(0);
        System.out.println("schemaEle = " + schemaEle);
        System.out.println("elemName = " + elemName);
        System.out.println("preXsdNs = " + preXsdNs);
        
        
        // get the element by name
        Element elem = null;
		for (Object e : schemaEle.getChildren()){
			if ((Element)e).getAttributeValue("name"));
		}
		
		
		if (elem == null) System.out.println("NG");
		System.out.println("elem = " + elem);
		*/
		//String term1 = "BLASTx";
		//String term2 = "blast";
		
		//System.out.println("definition score = " + CompareDefinition.getSimilarity(term1, term2));
		//System.out.println("term score = " + CompareTerm.getSimilarity(term1.toLowerCase(), term2.toLowerCase()));
		
		
	}
	
}
