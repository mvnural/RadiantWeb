package edu.uga.cs.wstool.parser.sawadl;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class Field {

	private String name;
    
	// use for annotation
    private Element element;
    private String modelRefernece;
    private List<Doc> docs;
	
	public Field(){
	}
	
	public Field(Element e, String name, String modelReference){
		this.element = e;
		this.name = name;
		this.modelRefernece = modelReference;
		this.docs = new ArrayList<Doc>();
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}


	public void setElement(Element element) {
		this.element = element;
	}


	public Element getElement() {
		return element;
	}


	public void setModelRefernece(String modelRefernece) {
		this.modelRefernece = modelRefernece;
	}


	public String getModelRefernece() {
		return modelRefernece;
	}

	public void setDocs(List<Doc> docs) {
		this.docs = docs;
	}

	public List<Doc> getDocs() {
		return docs;
	}
	
	
	
}
