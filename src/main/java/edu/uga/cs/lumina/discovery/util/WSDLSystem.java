package edu.uga.cs.lumina.discovery.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import edu.uga.radiant.util.DataManager;
import edu.uga.radiant.util.DatabaseManager;
import edu.uga.radiant.util.QueryManager;

public class WSDLSystem {
	
	private Connection conn;
	
	public WSDLSystem(Connection connection){
		conn = connection;
	}
	
	public ArrayList<String> search(String keyword) throws URISyntaxException, IOException, SQLException{
		
		ArrayList<String> result = QueryManager.search(conn, keyword);
		return result;
		
	}
	
	public String saveService(URL fileURL, String filename, String type, long provider){
		
		String result = "";
		
		try {
			
			// save it to a file:
            XMLOutputter outputer = new XMLOutputter();
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            // get xml document
            SAXBuilder builder = new SAXBuilder();
            Document document = (Document) builder.build(fileURL);
			
			// write to byte array
			outputer.output(document, out);
			
			// store in database
			String xml = new String(out.toByteArray(), "utf-8");
			String newMD5 = generateMD5(xml);
			
			long id = QueryManager.getServiceID(conn, filename);
			if (id != -1){
				String md5 = QueryManager.getMD5(conn, id);
				if (!newMD5.equals(md5)){
					DataManager.updateWSDL(conn, id, xml, newMD5);
				}
			}else{
				if (type.equalsIgnoreCase("wsdl")){
				    DataManager.storeWSDL(conn, filename, "", fileURL.toString(), provider, xml, newMD5);
				}else if (type.equalsIgnoreCase("sawsdl")){
				    DataManager.storeSAWSDL(conn, filename, "", fileURL.toString(), provider, xml, newMD5);
				}
			}
			
			out.close();
			return result;
			
		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
		}	
		return result;
	}

	public String saveWSDL(URL fileURL, String filename, long provider){
	    return saveService(fileURL, filename, "wsdl", provider);
	}
	
	public String saveSAWSDL(URL fileURL, String filename, long provider){
	    return saveService(fileURL, filename, "sawsdl", provider);
	}
	
	public static String generateMD5(String xml) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		
		byte[] defaultBytes = xml.getBytes("utf-8");
		MessageDigest algorithm = MessageDigest.getInstance("MD5");
		algorithm.reset();
		algorithm.update(defaultBytes);
		byte messageDigest[] = algorithm.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++) {
			hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
		}
		return hexString.toString();
		
	}
	
	public static void main(String args[]) throws MalformedURLException, SQLException {
	    
	    // HSQL
	    DatabaseManager mgr = new DatabaseManager("org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://localhost/", "radiant", "sa", "");
	    
	    // MySQL
	    //DatabaseManager mgr = new DatabaseManager("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/", "radiant", "root", "123456");
	    
        WSDLSystem sys = new WSDLSystem(mgr.getConnection());
        
	    
	    String wsdlBaseFileURL = "file:C:/Users/ucam10a/git/bitbucket/radiantweb/WebContent/XMLBox/WSDLBox/";
	    String[] wsfilenames = {"clustal.wsdl", "emboss_sixpack.wsdl", "NCBI_BLAST_(SOAP).wsdl", "WU-BLAST_(SOAP).wsdl"};
	    try{
	        for (String wsfilename : wsfilenames){
	            //System.out.println(wsfilename);
	            URL wsfileURL = new URL(wsdlBaseFileURL + wsfilename);
	            System.out.println(sys.saveWSDL(wsfileURL, wsfilename, 0));
	        }
	        ArrayList<String> wslist = QueryManager.getWSDLList(mgr.getConnection());
	        System.out.println("wsdl size = " + wslist.size());
	        for (String name : wslist){
	            System.out.println(name);
	        }
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    
	    try{
	        String sawsdlBaseFileURL = "file:C:/Users/ucam10a/git/bitbucket/radiantweb/WebContent/XMLBox/SAWSDLBox/";
	        String[] sawsfilenames = {"clustalw2.sawsdl", "fasta.sawsdl", "FilterSequencesWS.sawsdl", "muscle.sawsdl", "ncbiblast.sawsdl", "psiblast.sawsdl", "tcoffee.sawsdl", "WSConverter.sawsdl", "WSDbfetch.sawsdl", "wublast.sawsdl"};
	        for (String sawsfilename : sawsfilenames){
	            //System.out.println(sawsfilename);
	            URL sawsfileURL = new URL(sawsdlBaseFileURL + sawsfilename);
	            System.out.println(sys.saveSAWSDL(sawsfileURL, sawsfilename, 0));
	        }
	        ArrayList<String> sawslist = QueryManager.getSAWSDLList(mgr.getConnection());
	        System.out.println("sawsdl size = " + sawslist.size());
	        for (String name : sawslist){
	            System.out.println(name);
	        }
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    
	}
}