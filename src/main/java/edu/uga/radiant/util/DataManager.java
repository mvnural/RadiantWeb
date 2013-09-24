package edu.uga.radiant.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;

public class DataManager {

	public static TreeSet<String> stopWords = new TreeSet<String>();
	
	public DataManager(){
	}
	
	public static void storeService(Connection conn, String name, String description, String url, long provider) throws SQLException{
		
		PreparedStatement pstmt = null;
		String SQL = "";
		
		SQL = "INSERT INTO service VALUES ( ? , ? , ? , ? , ?);";
		
		pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, null);
		pstmt.setString(2, name);
		pstmt.setString(3, description);
		pstmt.setString(4, url);
		pstmt.setLong(5, provider);
		pstmt.executeUpdate();
		
	}
	
	public static void insertSAWSDL(Connection conn, long service_id, String xml, String md5) throws SQLException{
	    
	    PreparedStatement pstmt = null;
        String SQL = "";
        
        SQL = "INSERT INTO sawsdl VALUES ( ? , ? , ?, ?);";
        
        pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, null);
        pstmt.setString(2, md5);
        pstmt.setString(3, xml);
        pstmt.setLong(4, service_id);
        pstmt.executeUpdate();
        
    }
	
	public static void insertWSDL(Connection conn, long service_id, String xml, String md5) throws SQLException{
        
        PreparedStatement pstmt = null;
        String SQL = "";
        
        SQL = "INSERT INTO wsdl VALUES ( ? , ? , ?, ?);";
        
        pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, null);
        pstmt.setString(2, md5);
        pstmt.setString(3, xml);
        pstmt.setLong(4, service_id);
        pstmt.executeUpdate();
        
    }
	
	public static void storeSAWSDL(Connection conn, String name, String description, String url, long provider, String xml, String md5) throws SQLException{
	
	    storeService(conn, name, description, url, provider);
	    long id = QueryManager.getServiceID(conn, name);
	    if (id != -1){
	        insertSAWSDL(conn, id, xml, md5);
	    }else{
	        System.out.println("store service fail");
	    }
	    
	}
	
	public static void storeWSDL(Connection conn, String name, String description, String url, long provider, String xml, String md5) throws SQLException{
	    
        storeService(conn, name, description, url, provider);
        long id = QueryManager.getServiceID(conn, name);
        if (id != -1){
            insertWSDL(conn, id, xml, md5);
        }else{
            System.out.println("store service fail");
        }
        
    }
	
	public static boolean updateWSDL(Connection conn, long id, String xml, String md5) throws SQLException{
		
		boolean result = false;
		
		PreparedStatement pstmt = null;
		String SQL = "";
		
		SQL = "Update wsdl set xml = ?, md5 = ?, validate = ? where id = ? ;";
		
		pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, xml);
		pstmt.setString(2, md5);
		pstmt.setBoolean(3, false);
		pstmt.setLong(4, id);
		pstmt.executeUpdate();
		
		SAWSDLParser semP = null;
		try {
			
			semP = new SAWSDLParser(xml);
			String serviceDoc = semP.getServiceDoc();
			ArrayList<String> keywords = removeStopWord(serviceDoc);
			TreeSet<String> keywordSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			keywordSet.addAll(keywords);
			deleteKeyword(conn, id);
			for (String keyword : keywordSet){
				if ((keyword.length() < 100) && (!keyword.equals(""))){
					pstmt = null;
					SQL = "INSERT INTO keyword VALUES ( ? , ? );";
					pstmt = conn.prepareStatement(SQL);
					pstmt.setLong(1, id);
					pstmt.setString(2, keyword.trim());
					pstmt.executeUpdate();
				}
			}
			
			pstmt = null;
			SQL = "Update wsdl set validate  = ? where id = ? ;";
			pstmt = conn.prepareStatement(SQL);
			pstmt.setBoolean(1, true);
			pstmt.setLong(2, id);
			pstmt.executeUpdate();
			
			result = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	public static void deleteKeyword(Connection conn, long id) throws SQLException{
		
		PreparedStatement pstmt = null;
		String SQL = "DELETE FROM keyword WHERE id = ? ;";
		
		pstmt = conn.prepareStatement(SQL);
		pstmt.setLong(1, id);
		pstmt.executeUpdate();
		
	}
	
	public static ArrayList<String> removeStopWord(String Word) throws URISyntaxException, IOException {
		
		ArrayList<String> result = new ArrayList<String>();
		
		if (stopWords.size() == 0){
			stopWords = getStopWord();
		}
		for (String word : Word.split(" ")){
			
			word = word.trim().replaceAll("[`~!@#$%^&*()_+={}:;\"',<.>?/]", "");
            word = word.replace("[", "");
            word = word.replace("]", "");
			
			if (stopWords.contains(word.trim().toLowerCase())){
				continue;
			}
			result.add(word);
		}
		
		return result;
	}
	
	private static TreeSet<String> getStopWord() throws URISyntaxException, IOException {
		
		TreeSet<String> result = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		
		String fullFileURL = DataManager.class.getResource("").toString();
		
		File list = new File(new URI(fullFileURL + "stop.txt"));
		FileReader fread = new FileReader(list);
		BufferedReader in = new BufferedReader(fread);
	    String str;
	    while ((str = in.readLine()) != null) {
	    	result.add(str.trim());
	    }
		in.close();
		fread.close();
		
		return result;
	}

	public static String tokenize(String searchWord) throws URISyntaxException, IOException {
		
		String result = "";
		List<String> words = DataManager.removeStopWord(searchWord);
		boolean start = true;
		for (String word : words){
			if (start == true){
				result = word;
				start = false;
			}else{
				result = result + ", " + word;
			}
		}
		return result;
	}
	
	public static void insertNewUserAccount(Connection conn, String username, String password, String userType) throws SQLException{
        
        PreparedStatement pstmt = null;
        String SQL = "INSERT INTO user VALUES (?, ?, ?, ?) ;";
        
        pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, null);
        pstmt.setString(2, username);
        pstmt.setString(3, password);
        pstmt.setString(4, userType);
        pstmt.executeUpdate();
        
    }
	
}
