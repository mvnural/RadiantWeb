/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uga.radiant.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Chaitu, Yung Long Li
 */
public class RadiantToolConfig {

	private static String propertyFile;
	
    private static String dbDriver;

    private static String dbUrl;

    private static String dbName;

    private static String dbUserId;

    private static String dbPassword;
    
    private static String operationConcept;
    
    private static String operationConcept2;

    private static String parameterConcept;

    private static String definitionURI;
    
    private static String descriptionURI;

    private static String wsdlUrl;
    
    private static String updatedWSDLLocation;
    
    private static String WADLLocation;
    
    private String updatedWADLLocation;
    
    private static String existConfiguration;
    
    private static String stopwordsPath;
    
    private static String env;
    
    public RadiantToolConfig(){//String property){
        
        try{
            
            ClassLoader loader = RadiantToolConfig.class.getClassLoader();
            String url = loader.getResource("RadiantProperties.properties").toString();
            propertyFile = url; // + "RadiantProperties.properties";
            
            File file = new File(new URI(propertyFile));
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream(file);
            prop.load(in);
            
            dbName = prop.getProperty("db.name");
            dbUserId = prop.getProperty("db.userId");
            dbPassword = prop.getProperty("db.password");
            dbDriver = prop.getProperty("db.driver");
            dbUrl = prop.getProperty("db.url");
            operationConcept = prop.getProperty("operation.concept");
            operationConcept2 = prop.getProperty("operation.concept2");
            parameterConcept = prop.getProperty("parameter.concept");
            definitionURI = prop.getProperty("definition.uri");
            wsdlUrl = prop.getProperty("wsdl.url");
            updatedWSDLLocation = prop.getProperty("updatedwsdl.location");
            WADLLocation = prop.getProperty("wadl.location");
            setUpdatedWADLLocation(prop.getProperty("updatedwadl.location"));
            existConfiguration=prop.getProperty("exist.configuraion");
            descriptionURI=prop.getProperty("description.uri");
            //stopwordsPath=prop.getProperty("stopword.path");
            String fullFileBase = loader.getResource("").toString();
            stopwordsPath = fullFileBase + "stop.txt";
            env=prop.getProperty("env");
            
            in.close();
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println( "Property-Value pair could not be added\nPlease check if the .properties file exists at the said path..!!");
        }
    }
    
    /** the logger */
	private static Logger logger;
    
    public static String getStopWordsPath(){
        if (stopwordsPath == null) new RadiantToolConfig();
        return stopwordsPath;
    }
    
    public static String getDescriptionURI(){
        if (descriptionURI == null) new RadiantToolConfig();
        return descriptionURI;
    }
    
    public static String getExistConfiguration(){
        if (existConfiguration == null) new RadiantToolConfig();
        return existConfiguration;
    }

    public static String getWsdlUrl(){
        if (wsdlUrl == null) new RadiantToolConfig();
        return wsdlUrl;
    }

    public void setWsdlUrl(String url){
        wsdlUrl=url;
    }

    public static String getDefinitionURI(){
        if (definitionURI == null) new RadiantToolConfig();
        return definitionURI;
    }

    public static String getOperationConcept(){
        if (operationConcept == null) new RadiantToolConfig();
    	return operationConcept;
    }

    public static String getParameterConcept(){
        if (parameterConcept == null) new RadiantToolConfig();
        return parameterConcept;
    }

    public static String getDbDriver(){
        if (dbDriver == null) new RadiantToolConfig();
        return dbDriver;
    }
    
    public static String getDbUrl(){
        if (dbUrl == null) new RadiantToolConfig();
        return dbUrl;
    }
    
    public static String getDbUserId(){
        if (dbUserId == null) new RadiantToolConfig();
        return dbUserId;
    }
    
    public static String getDbPassword(){
        if (dbPassword == null) new RadiantToolConfig();
        return dbPassword;
    }
    
    public static String getDbName(){
        if (dbName == null) new RadiantToolConfig();
        return dbName;
    }
    
    public static String getUpdateWSDLLocation(){
        if (updatedWSDLLocation == null) new RadiantToolConfig();
        return updatedWSDLLocation;
    }
    
    public void setExistConfiguration(String config){
        RadiantToolConfig.existConfiguration = config;
    }
            
            
    public void setUpdateWSDLLocation(String location){
        RadiantToolConfig.updatedWSDLLocation = location;
    }
    
    public void setOperationConcept(String operationConcept){
        RadiantToolConfig.operationConcept = operationConcept;
    }
    
    public void setParameterConcpet(String parameterConcept){
        RadiantToolConfig.operationConcept = parameterConcept;
    }
    
    public void setDbUrl(String dbUrl)
    {
        RadiantToolConfig.dbUrl = dbUrl;
    }
    
    public void setDbName(String _dbName)
    {
        dbName = _dbName;
    }
    
    public void setDbUserId(String _dbUserId)
    {
        dbUserId = _dbUserId;
    }
    
    public void setDbPassword(String _dbPassword)
    {
        dbPassword = _dbPassword;
    }
    
    public void setDbDriver(String dbDriver)
    {
        RadiantToolConfig.dbDriver = dbDriver;
    }
    
    public void setDefinitionURI(String definitionURI){
        RadiantToolConfig.definitionURI = definitionURI;
    }
    
    public void setDescriptionURI(String descriptionURI){
        RadiantToolConfig.descriptionURI = descriptionURI;
    }
    
    public void setUpdatedWADLLocation(String updatedWADLLocation) {
		this.updatedWADLLocation = updatedWADLLocation;
	}

	public String getUpdatedWADLLocation() {
		return updatedWADLLocation;
	}
	
	public void setWADLLocation(String wADLLocation) {
		WADLLocation = wADLLocation;
	}

	public String getWADLLocation() {
	    if (WADLLocation == null) new RadiantToolConfig();
		return WADLLocation;
	}

    //Sets the Property - value pair for the values passed to it
    //Returns Status: Successfully added / Not added
    public String setValuesToProperty(String property,String value){
    
    	try
        {
        	//Creating an object of java.util.Properties
        	Properties prop = new Properties();
        	//Loading the properties file as a Input file
        	FileInputStream in = new FileInputStream(propertyFile);
        	prop.load(in);
        	in.close();
        	//Uses SetProperty method defined in java.util.Properties to add property(key)-Value pair,
        	//both of them strings to the object of type Properties created.
        	prop.setProperty(property, value);
        	//Stores the modified object to the properties file
        	FileOutputStream out = new FileOutputStream(propertyFile);
        	prop.store(out, null);
        	out.close();
        	return "Property-Value pair successfully added..!!";
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	return "Property-Value pair could not be added\nPlease check if the .properties file exists at the said path..!!";
        }
    }

	public void setOperationConcept2(String operationConcept2) {
	    RadiantToolConfig.operationConcept2 = operationConcept2;
	}

	public String getOperationConcept2() {
		return operationConcept2;
	}

	public static String getEnv() {
        return env;
    }

    public static void setEnv(String env) {
        RadiantToolConfig.env = env;
    }
	
	/**
	 * get the logger
	 * @return logger
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws URISyntaxException 
	 */
	public static Logger getLogger(){
		
		if (logger == null){
			
			try {
				
			    ClassLoader loader = RadiantToolConfig.class.getClassLoader();
                String logPropPath = loader.getResource("Log4j.properties").toString();
                File logPropFile = new File(new URL(logPropPath).toURI());
                Properties logp = new Properties();
                logp.load(new FileInputStream(logPropFile));
                PropertyConfigurator.configure(logp);
                logger = Logger.getLogger("J2EE");
			    
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
		}
		
		return logger;
	}
	
	public static void main(String args[]){
    	
    	RadiantToolConfig config = new RadiantToolConfig();
    	
    	System.out.println("wadl = " + config.getUpdatedWADLLocation());
    	System.out.println("wadl = " + config.getWADLLocation());
    	
    }
    
}
