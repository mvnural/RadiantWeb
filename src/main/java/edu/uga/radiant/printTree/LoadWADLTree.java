/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.radiant.printTree;

import edu.uga.cs.wstool.parser.sawadl.Application;
import edu.uga.cs.wstool.parser.sawadl.Doc;
import edu.uga.cs.wstool.parser.sawadl.Field;
import edu.uga.cs.wstool.parser.sawadl.Grammar;
import edu.uga.cs.wstool.parser.sawadl.Method;
import edu.uga.cs.wstool.parser.sawadl.Param;
import edu.uga.cs.wstool.parser.sawadl.Request;
import edu.uga.cs.wstool.parser.sawadl.Resource;
import edu.uga.cs.wstool.parser.sawadl.Resources;
import edu.uga.cs.wstool.parser.sawadl.WADLParser;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.jdom.Namespace;

/**
 *
 * @author Chaitu, Yung Long Li
 */
public class LoadWADLTree {
	
	static Namespace SAWADLNamespace = WADLParser.getSAWADLNamespace();
    
    public static void loadWADL(WADLParser wsParser , StringBuffer buf, String filename){
        
    	try {
            Application app = wsParser.getApplicationOfWADL();
            List<Resources> resources = app.getResources();
            buf.append("<div class='ui-widget-content ui-corner-all ui-state-default' style='width:95%; margin:6px;padding:6px'>"
                    + "<span style=\"width:40%;float:left;padding:2px\">" + filename + "</span>"
                    + "<span id='save' style=\"color:white;background:#616D7E;padding:3px;margin-right:3px\" onclick=\"save();\" "
                    + "class=\"ui-button ui-corner-all\">Save WADL</span>"
                    + "<span id=\"recommend\" class=\"ui-button ui-corner-all\" "
                    + "style=\"color:white;background:#616D7E;padding:3px;margin-right:3px;\" onclick='recommendTerms()'>Recommend Terms</span> "
                    + "<span id=\"switchWSView\" style=\"color:white;background:#616D7E;padding:3px;\" class=\"ui-button ui-corner-all\">WADLXML view</span></div>");
            
            buf.append("<div id=\"wsxmlview\" style=\"widh:100%; overflow: auto\"></div>");
            buf.append("<div id=\"wstree\" class='ui-widget-content ui-corner-all' "
            		+ "style='overflow:auto;margin:6px; height:475px; padding:10px'>");
            
            if(resources != null){
            	buf.append("<ul>");
                for(Resources ress : resources){
                    String ressName = ress.getBase().getPath();
                	buf.append("<li id=''>"
                    		+ "<div id=\"" + ressName + "\" class=\"drop ui-widget-content ui-corner-all\" >"
                        	+ "<span style='width:80%;'> Resource : " + ressName + "</span>"
                        	+ "</div>");
                    List<Resource> res = ress.getResources();
                    if(res != null){
                    	buf.append("<ul>");
                        for(Resource resource : res){
                            printResource(resource, buf);
                        }
                        buf.append("</ul>");
                    }
                }
                buf.append("</ul>");
            }
            
            // print the response
            Grammar grammar = app.getGrammar();
            if(grammar != null){
            	buf.append("<ul>");
            	buf.append("<li id=''>"
                		+ "<div id=\"response\" class=\"drop ui-widget-content ui-corner-all\" >"
                    	+ "<span style='width:80%;'> Response : response </span>"
                    	+ "</div>");
            	List<Field> fields = grammar.getFields();
            	if(fields != null){
                	buf.append("<ul>");
                    for(Field field : fields){
                        printField(field, buf);
                    }
                    buf.append("</ul>");
                }
            	buf.append("</ul>");
            }
            
            buf.append("</div>");
        
        } catch (Exception ex) {
            Logger.getLogger(LoadWADLTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void printResource(Resource res, StringBuffer buf) throws IOException{
    	
    	List<Param> resParams = res.getParams();
    	String resPath = res.getPath();
        buf.append("<li id=''>"
        		+ "<div id=\"" + resPath + "\" class=\"drop ui-widget-content ui-corner-all\" >"
                + "<span style='width:80%;'> Resource : " + resPath + "</span>"
                + "</div>");
        buf.append("<ul>");
            
        if(resParams != null){
            for(Param resParam : resParams){ // to display the resource paramters
            	printParam(resParam, buf);
            }    
        }else{
            //Response doesnot have parameters. Not an error
        }
        
        List<Method> resMethods = res.getMethods();
        if(resMethods != null){
            for(Method method : resMethods){
            	printMethod(method, buf);
            }
            buf.append("</ul>");
            buf.append("</li>");  
        }
    }
    
    public static void printMethod(Method method, StringBuffer buf) throws IOException{ 	
    	
    	String methodId = method.getId();
        String methodName = methodId + ":" + method.getName();
        
        List<Doc> mDocs = method.getDocs();
        String def = "";
        if(mDocs != null){
            for(Doc mdoc : mDocs){
                def += (" " + mdoc.getInnerText());
            }
            def = cutLine(def);
        }
        
        buf.append("<li id=''>"
                + "<div id=\"" + methodName + "\" class=\"operation drop ui-widget-content ui-corner-all ui-droppable\" title=\"" + def + "\" value=\"operation:" + methodName + "\" >"
                + "<span style='width:50%; float:left;'>" + methodName + "</span>"
                + "<span class=\"ui-button ui-corner-all\" style=\"background:#616D7E; color:white; text-align: center; width: 120px; margin: 2px; padding:1px\" "
                + "onclick=\"recommend('0', '" + methodName + "','method','" + def + "')\" "
                + ">Recommend Term</span>"
                + "</div>");
        buf.append("<div id=\"" + methodId + method.getName() + "MethodAnnotation\" name=\"method-" + methodName + "-annotation\" class=\"annotation\" value=\"" + "method:" + methodName + "\">");
        
        // get modeReference
        String concept = method.getModelReference();
        if(concept != null && !concept.equals("")){
        	buf.append("<div style=\"margin:10px\">");
        	buf.append("<span id=\"1\" class=\"preExisting hove ui-widget-content ui-corner-all\" >" + concept + "</span>");
        	buf.append("<span id=\"remove1\" class=\"ui-icon ui-icon-close hove\" style=\"float:left\" onclick=\"removeElement(this.id)\" value=\"" + concept + "\" ></span>");
        	buf.append("</div>");
        }
        
        // get lower schema mapping
        // not implement
        
        // get lift schema mapping
        // not implement
        
        buf.append("</div>");
        
        Request req = method.getRequest();
        List<Param> reqParams = null;
        if(req != null){
            reqParams = req.getParams();
            if(reqParams != null)
            {
                buf.append("<ul>");
                for(Param p : reqParams){
                	printParam(p, buf);
                }
                buf.append("</ul>");
            }
        }else{
            //Request is null - Error
        }
        
        buf.append("</li>");

    }

    public static void printParam(Param param, StringBuffer buf) throws IOException{ 	
    	String paramName = param.getName();
        String def = "";
        List<Doc> docs = param.getDocs();
        for(Doc doc : docs){
            def += (" " + doc.getInnerText());
        }
        def = cutLine(def);
        buf.append("<li id=>"
                + "<div id=\"" + paramName + "\" class=\"element drop ui-widget-content ui-corner-all ui-droppable\" title=\" " + def + " \" value=\"param:" + paramName + "\" >"
                + "<span style='width:40%; float:left;'>" + paramName + "</span>"
                + "<span class=\"ui-button ui-state-default ui-corner-all\" style=\"background:#616D7E;color: white; text-align:center;;width:120px;margin:2px\" "
                + "onclick=\"recommend('0', '" + paramName + "','param','" + def + "')\" "
                + ">Recommend Term</span>"
                + "<span class=\"ui-button ui-state-default ui-corner-all\" style=\"text-align:center;background:#616D7E;color:white;width:120px;margin:2px\" onclick=\"addWadlSchemaMapper('" + paramName + "','param')\">Add SchemaMapper</span>"
                + "</div>");
        buf.append("<div id=\"" + paramName + "ParamAnnotation\" name=\"param-" + paramName + "-annotation\" class=\"annotation\" value=\"" + "param:" + paramName + "\">");
        Attribute attr = param.getElement().getAttribute("modelReference", SAWADLNamespace);
        if(attr != null && attr.getValue() != null && !attr.getValue().isEmpty()){
        	buf.append("<div style=\"margin:10px\">");
        	buf.append("<span id=\"1\" class=\"preExisting hove ui-widget-content ui-corner-all\" value=\"" + attr.getValue() + "\">" +
        			attr.getValue() + "</span>");
        	buf.append("<span id=\"remove1\" class=\"ui-icon ui-icon-close hove\" style=\"float:left\" onclick=\"removeElement(this.id)\" value=\"" + attr.getValue() + "\" ></span>");
        	buf.append("</div>");
        }
        
        // get lower schema mapping
        // not implement
        
        // get lift schema mapping
        // not implement
        
        buf.append("</div></li>");
    }
    
    public static void printField(Field field, StringBuffer buf) throws IOException{ 	
    	String fieldName = field.getName();
        String def = "";
        List<Doc> docs = field.getDocs();
        for(Doc doc : docs){
            def += (" " + doc.getInnerText());
        }
        def = cutLine(def);
        buf.append("<li id=>"
                + "<div id=\"" + fieldName + "\" class=\"element drop ui-widget-content ui-corner-all ui-droppable\" title=\" " + def + " \" value=\"param:" + fieldName + "\" >"
                + "<span style='width:40%; float:left;'>" + fieldName + "</span>"
                + "<span class=\"ui-button ui-state-default ui-corner-all\" style=\"background:#616D7E;color: white; text-align:center;;width:120px;margin:2px\" "
                + "onclick=\"recommend('0', '" + fieldName + "','param','" + def + "')\" "
                + ">Recommend Term</span>"
                + "<span class=\"ui-button ui-state-default ui-corner-all\" style=\"text-align:center;background:#616D7E;color:white;width:120px;margin:2px\" onclick=\"addWadlSchemaMapper('" + fieldName + "','param')\">Add SchemaMapper</span>"
                + "</div>");
        buf.append("<div id=\"" + fieldName + "ParamAnnotation\" name=\"param-" + fieldName + "-annotation\" class=\"annotation\" value=\"" + "param:" + fieldName + "\">");
        Attribute attr = field.getElement().getAttribute("modelReference", SAWADLNamespace);
        if(attr != null && attr.getValue() != null && !attr.getValue().isEmpty()){
        	buf.append("<div style=\"margin:10px\">");
        	buf.append("<span id=\"1\" class=\"preExisting hove ui-widget-content ui-corner-all\" value=\"" + attr.getValue() + "\">" +
        			attr.getValue() + "</span>");
        	buf.append("<span id=\"remove1\" class=\"ui-icon ui-icon-close hove\" style=\"float:left\" onclick=\"removeElement(this.id)\" value=\"" + attr.getValue() + "\" ></span>");
        	buf.append("</div>");
        }
        
        // get lower schema mapping
        // not implement
        
        // get lift schema mapping
        // not implement
        
        buf.append("</div></li>");
    }
    
    
	public static String cutLine(String def) throws IOException {
		
		String result = "";
		
		char[] b = def.toCharArray();
		StringBuffer buf = new StringBuffer();
		boolean angleStart = false;
		for (int i = 0; i < b.length; i++){
			
			if(b[i] == '<') {
				angleStart = true;
			}else if(b[i] == '>'){
				angleStart = false;
			}else if(b[i] == '"' || b[i] == '\''){
				buf.append(' ');
			}else{
				if (angleStart == false) buf.append(b[i]);
			}
		}
		
		b = buf.toString().toCharArray();
	    BufferedReader in = new BufferedReader(new CharArrayReader(b));
		String str;
		while ((str = in.readLine()) != null){
			result = result + " " + str;  
		}
		
		return result;
	}
    
}
