package edu.uga.radiant.util;

import org.jdom.Document;
import org.jdom.Element;


public class CheckDocumentType{
    
    public static double getWSDLVersion(Document doc){
        double version=0.0;
        
        Element root = doc.getRootElement();
        String rootTag = root.getName();
        if(rootTag.equalsIgnoreCase("description"))
            version = 2.0;
        else
            if(rootTag.equalsIgnoreCase("definitions"))
                version = 1.1;
        return version;
    }
    
    public static boolean isWSDL(Document doc){
        boolean iswsdl = false;
        Element root = doc.getRootElement();
        String rootTag = root.getName();
        if(rootTag.equalsIgnoreCase("description") || rootTag.equalsIgnoreCase("definitions"))
            iswsdl = true;
        return iswsdl;
    }
    
    public static boolean isWADL(Document doc){
        boolean iswadl = false;
        Element root = doc.getRootElement();
        String rootTag = root.getName();
        if(rootTag.equalsIgnoreCase("application"))
            iswadl = true;
        return iswadl;
    }
    
}