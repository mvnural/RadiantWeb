package edu.uga.cs.radiant.struts2.action;

public class Service {
	private String name;
	private String description;
	private String md5;
	private String XML;
	private int id;
	private int providerId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getXML() {
		return XML;
	}
	public void setXML(String xML) {
		XML = xML;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProviderId() {
		return providerId;
	}
	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

}
