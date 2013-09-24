/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uga.radiant.printTree;

import edu.uga.cs.wstool.parser.xml.XMLParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;

import org.jdom.Document;

/**
 *
 * @author Chaitu, Yung-Long Li
 */
public class LoadXml {
	
	public static void loadWSDLXml(Document doc , StringBuffer buf) throws IOException{
        
		ByteArrayInputStream wsdlData = new ByteArrayInputStream(XMLParser.updateToXml(doc));
        BufferedReader wsdlBuffer = new BufferedReader(new InputStreamReader(wsdlData));

        String line = null, actLine = null;
        int lineNumber = 0;
        boolean comment = false;
        while((line = wsdlBuffer.readLine()) != null){
            if(line.contains("<?xml ")){
                continue;
            }
            if(line.contains("<!--")){
                if(line.contains("-->")){
                    String unCommentedPart = line.substring(line.indexOf("-->")+3, line.length()).trim();
                    if(unCommentedPart.isEmpty()){ //only comment
                        continue;
                    }else{
                        line = unCommentedPart;
                    }
                }else{
                    comment = true;
                    continue;
                }
            }else if(comment){
                if(line.contains("-->")){
                    comment = false;
                    String unCommentedPart = line.substring(line.indexOf("-->")+3, line.length()).trim();
                    if(unCommentedPart.isEmpty()) //only comment
                    {   
                        continue ;
                    }else{
                        line = unCommentedPart;
                    }
                }else{
                	continue;
                }
            }
            
            actLine = line.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            if(actLine.contains("<") && actLine.contains(">") ){
                int actualSize = actLine.trim().split(" ").length;
                String[] lineParts = actLine.split(" ");
                boolean annotStart = false;
                for(int i = 0; i < lineParts.length; i++){
                    if(lineParts[i].contains("<")){	// Adds span to the wsdl name tags
                        int indexStart = lineParts[i].indexOf("<");
                        int indexEnd;
                        int length = lineParts[i].length(); 
                        if (lineParts[i].contains(">")){
                            indexEnd = lineParts[i].indexOf(">");
                        }else{ 
                        	indexEnd = length;
                        }
                        if(lineParts[i].contains("</") && actualSize>1){ 
                            try{
                            	actLine = actLine + lineParts[i].substring(0, indexStart+1) + "<span class=\"wsdltagname\">" + lineParts[i].substring(indexStart+1, indexEnd) + "</span>";
                            }catch(Exception ex){
                            //    String s = actLine;
                            //    int x = 0;
                                System.out.println("Error in WSDLXML view : " + actLine);
                            }
                        }else{
                            actLine = lineParts[i].substring(0, indexStart+1)+"<span class=\"wsdltagname\">"+lineParts[i].substring(indexStart+1, indexEnd)+"</span>";
                        }
                        if(indexEnd != length) actLine = actLine+lineParts[i].substring(indexEnd, length);
                    }else{	// Adds span tgs to the wsdl component attributes
                        if(lineParts[i].contains("=")){
                            int index = lineParts[i].indexOf("=");
                            String attrName = lineParts[i].substring(0, index);
                            String attrvalue;
                            String attr = "";
                            String attrvalueclass;
                            String attrnameclass;
                            if(attrName.contains("modelReference") || attrName.contains("loweringSchemaMapping") || attrName.contains("liftingSchemaMapping")){
                                attrvalueclass = "wsdlannotation";
                                attrnameclass = "wsdlannotation";
                                annotStart = true;
                            }else{
                                attrvalueclass = "wsdlattrvalue";
                                attrnameclass = "wsdlattrname";
                            }
                            // checking for the last attribute because we have to handle the ">" at the end
                            if(lineParts[i].contains(">")){ 
                                int indexgt = lineParts[i].indexOf(">");
                                attrvalue = lineParts[i].substring(index+1,indexgt);
                                if(annotStart){
                                    if(attrvalue.startsWith("\"")){
                                    	attr = "<span class='" + attrnameclass + "'>" + attrName + "</span>=<span class=\"" + attrvalueclass + "\">" + attrvalue;
                                    }else if(attrvalue.endsWith("\"")){
                                        attr += " " + attrvalue + "</span>"; 
                                    }else{
                                        attr += " " + attrvalue;
                                    }
                                }else{
                                	attr = "<span class='"+attrnameclass+"'>" + attrName + "</span>=<span class=\""+attrvalueclass+"\">"+attrvalue+"</span>";
                                }
                                actLine = actLine + " " + attr + lineParts[i].substring(indexgt, lineParts[i].length());
                            }else{
                                attrvalue = lineParts[i].substring(index+1, lineParts[i].length());
                                attr = "<span class=\"" + attrnameclass + "\">" + attrName + "</span>=<span class=\""+attrvalueclass+"\">"+attrvalue+"</span>";
                                actLine = actLine + " " + attr;
                            }
                        }else{
                        	if(annotStart){
                        		actLine = actLine + " <span class='wsdlannotation'>" + lineParts[i] + "</span>";
                            }else{
                            	actLine = actLine + " " + lineParts[i];
                            }
                        }
                    }
                }
            }
            
            buf.append("<div id='" + lineNumber + "'>" + actLine + "</div>");
            lineNumber++;
        }
        wsdlBuffer.close();
        wsdlData.close();
		     
    }
	
	public static void loadWADLXml(Document doc, StringBuffer buf) throws IOException {
		
		ByteArrayInputStream wsdlData = new ByteArrayInputStream(XMLParser.updateToXml(doc));
        BufferedReader wadlBuffer = new BufferedReader(new InputStreamReader(wsdlData));
        
        String line = null, actLine = null;
        int lineNumber = 0;
        int indent = 1;
        boolean comment = false;
        
        while((line = wadlBuffer.readLine()) != null){

            if(line.contains("<?xml ")){
                continue;
            }
            if(line.contains("<!--")){
                if(line.contains("-->")){
                    String unCommentedPart = line.substring(line.indexOf("-->")+3, line.length()).trim();
                    if(unCommentedPart.isEmpty()){ //only comment
                        continue;
                    }else{
                        line = unCommentedPart;
                    }
                }else{
                    comment = true;
                    continue;
                }
            }else if(comment){
                if(line.contains("-->")){
                    comment = false;
                    String unCommentedPart = line.substring(line.indexOf("-->")+3, line.length()).trim();
                    if(unCommentedPart.isEmpty()){ //only comment   
                        continue ;
                    }else{
                        line = unCommentedPart;
                    }
                }else{
                	continue;
                }
            }
            // put indent
            actLine = line.trim();
            if (actLine != null && actLine.startsWith("<") && !actLine.contains("</") && !actLine.endsWith("/>")) {
            	if (!actLine.startsWith("<app")){
            		actLine = addIndent(actLine, indent);
                	indent += 1;
            	}
            }else if (actLine != null && actLine.startsWith("<") && actLine.endsWith(">") && !actLine.endsWith("/>") && !actLine.startsWith("</") ){
        		actLine = addIndent(actLine, indent);
        		indent += 1;
        	}
            
            if (actLine != null) {
            	if (actLine.startsWith("<") && actLine.endsWith("/>")){
            		actLine = addIndent(actLine, indent);
            	}else if (actLine.startsWith("</")) {
            		indent -= 1;
                	actLine = addIndent(actLine, indent);
            	}else if (actLine.startsWith("<") && !actLine.startsWith("</") && actLine.contains("</")){
            		actLine = addIndent(actLine, indent);
            	}else if (!actLine.startsWith("<") && actLine.contains("</") && actLine.endsWith(">")){
            		indent -= 1;
            	}
            }

            
            actLine.replace("&","&amp;");
            actLine = actLine.replace("<", "&lt;");
            actLine = actLine.replace(">", "&gt;");
            String printLine = "";
            
            
            
            
            if(actLine.contains("&lt;") && actLine.contains("&gt;") ){
            	
            	String[] lineParts = splitToArray(actLine);
                int actualSize = lineParts.length;
                for(int i = 0; i < lineParts.length; i++){
                	if(lineParts[i].contains("&lt;") && (!lineParts[i].contains("&lt;![CDATA["))){ // Adds span to the wsdl name tags
                        int indexStart = lineParts[i].indexOf("&lt;");
                        int indexEnd;
                        int length = lineParts[i].length(); 
                        if(lineParts[i].contains("&gt;")){
                            indexEnd = lineParts[i].lastIndexOf("&gt;");
                        }else{ 
                        	indexEnd = length;
                        }
                        if(lineParts[i].contains("&lt;/") && actualSize > 1){ 
                        	printLine = printLine + lineParts[i].substring(0, indexStart+4)+"<span class=\"wsdltagname\">"+lineParts[i].substring(indexStart+4, indexEnd)+"</span>";
                        }else{
                        	printLine = printLine + lineParts[i].substring(0, indexStart+4) + "<span class=\"wsdltagname\">"+lineParts[i].substring(indexStart+4, indexEnd)+"</span>";
                        }
                        if(indexEnd != length){
                        	printLine = printLine + lineParts[i].substring(indexEnd, length);
                        }
                    }else{ // Adds span tgs to the wsdl component attributes
                        if(lineParts[i].contains("=")){
                            int index = lineParts[i].indexOf("=");
                            String attrName = lineParts[i].substring(0, index);
                            String attrvalue;
                            String attr;
                            String attrvalueclass;
                            String attrnameclass;
                            if(attrName.contains("modelReference") || attrName.contains("SchemaMapping")){
                                attrvalueclass = "wsdlannotation";
                                attrnameclass = "wsdlannotation";
                            }else{
                                attrvalueclass = "wsdlattrvalue";
                                attrnameclass = "wsdlattrname";
                            }
                            // checking for the last attribute because we have to handle the ">" at the end
                            if(lineParts[i].contains("&gt;")){ 
                                int indexgt = lineParts[i].indexOf("&gt;");
                                attrvalue = lineParts[i].substring(index+1,indexgt);
                                attr = "<span class='"+attrnameclass+"'>"+attrName+"</span>=<span class=\""+attrvalueclass+"\">"+attrvalue+"</span>";
                                printLine = printLine + " " + attr + lineParts[i].substring(indexgt, lineParts[i].length());
                            }else{
                                attrvalue = lineParts[i].substring(index+1, lineParts[i].length());
                                attr = "<span class=\"" + attrnameclass + "\">" + attrName + "</span>=<span class=\"" + attrvalueclass + "\">" + attrvalue + "</span>";
                                printLine = printLine + " " + attr;
                            }
                        }else{
                        	printLine = printLine + " " + lineParts[i];
                        }
                    } //if
                } // for
            }
            
            buf.append("<div id='" + lineNumber + "'>" + printLine + "</div>");
            lineNumber++;
        }
        
        wadlBuffer.close();
        wsdlData.close();
	}
	
	

	/**
    * @param actLine
    * @return String
    */
	public String trim(String actLine){
		String []temp = actLine.split("<");
		temp[1] = "<span class='sugg'><" + temp[1] + "</span>";
		actLine = temp[0] + temp[1];
		return actLine;
	}
	
	/**
    * @param actLine
    * @return spliting words
    */
	public static String[] splitToArray(String actLine) {
    	String[] parts = actLine.split(" ");
    	ArrayList<String> temp = new ArrayList<String>();
    	for(String part : parts){
    		int indexStart = part.indexOf("&lt;");
            int indexEnd;
            int length = part.length(); 
            //actLine = "<span class=\"wsdltagname\">"+lineParts[0]+"</span>";
            if(part.contains("&gt;")){
                indexEnd = part.lastIndexOf("&gt;");
            }else{ 
            	indexEnd = length;
            }
            if(indexStart > indexEnd){
            	temp.add(part.substring(0, indexEnd + 4));
            	temp.add(part.substring(indexEnd + 4, length));
            }else{
            	temp.add(part);
            }
    	}
    	String[] result = new String[temp.size()];
    	return temp.toArray(result);
	}
	
	private static String addIndent(String actLine, int indent) {
		
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < indent; i++){
			result.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		result.append(actLine);
		return result.toString();
	
	}	
}
