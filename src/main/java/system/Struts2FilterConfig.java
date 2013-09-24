package system;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Struts2FilterConfig {

	private Set<String> terms = new HashSet<String>();
	private static String expression;
	
	public Struts2FilterConfig(){

		try{
    		
    		String url = Struts2FilterConfig.class.getResource("").toString();
    		String propertyFile = url + "Struts2FilterTerm.properties";
    		File file = new File(new URI(propertyFile));
        	Properties prop = new Properties();
        	prop.load(new FileInputStream(file));
        	expression = prop.getProperty("filterTerms");
        	String[] elements = expression.split(",");
        	for (String term : elements){
        		terms.add(term.trim());
        	}
        	
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	System.out.println( "Property-Value pair could not be added\nPlease check if the .properties file exists at the said path..!!");
        }
	}
	
	
	public static void setExpression(String expression) {
		Struts2FilterConfig.expression = expression;
	}
	
	public static String getExpression() {
		return expression;
	}

	public void setTerms(Set<String> terms) {
		this.terms = terms;
	}

	public Set<String> getTerms() {
		return terms;
	}
		
}
