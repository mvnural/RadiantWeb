package edu.uga.cs.lumina.discovery.util;

import java.util.List;

/**
 * 
 * @author Long
 *
 * the Object which is used to represent the operation in wsdl file
 */
public class OperationInfo {

	private String service;
	private String servicepath;
	private String operation;
	private String doc;
	private List<String> inputs;
	private List<String> outputs;
	private double score;
	
	
	public void setService(String service) {
		this.service = service;
	}
	public String getService() {
		return service;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getOperation() {
		return operation;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getScore() {
		return score;
	}
	public void setServicepath(String servicepath) {
		this.servicepath = servicepath;
	}
	public String getServicepath() {
		return servicepath;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public String getDoc() {
		return doc;
	}
	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}
	public List<String> getOutputs() {
		return outputs;
	}
	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}
	public List<String> getInputs() {
		return inputs;
	}
	
}
