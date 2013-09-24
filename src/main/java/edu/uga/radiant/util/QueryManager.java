package edu.uga.radiant.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryManager {

    public static long getUserID(Connection conn, String username) throws SQLException{
        
        long result = -1;
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String SQL = "Select id from user where username = ? ;";
        
        pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, username);
        rs = pstmt.executeQuery();
        
        // put result into classOBJs
        while (rs.next()) {
            result = rs.getLong(1);
        }
        return result;
        
    }
    
	public static long getServiceID(Connection conn, String name, String type) throws SQLException{
		
		long result = -1;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "";
		if (type.equalsIgnoreCase("sawsdl")){
		    SQL = "Select S.id from service S, sawsdl SA where name = ? and SA.service_id = S.id ;";
		}else if (type.equalsIgnoreCase("wsdl")){
		    SQL = "Select S.id from service S, wsdl W where name = ? and W.service_id = S.id ;";
		}
		
		pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, name);
		rs = pstmt.executeQuery();
		
		// put result into classOBJs
		while (rs.next()) {
			result = rs.getLong(1);
		}
		return result;
		
	}
	
	public static long getServiceID(Connection conn, String name) throws SQLException{
        
        long result = -1;
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String SQL = "";
        SQL = "Select S.id from service S where name = ? ;";
        
        pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, name);
        rs = pstmt.executeQuery();
        
        // put result into classOBJs
        while (rs.next()) {
            result = rs.getLong(1);
        }
        return result;
        
    }
	
	public static String getMD5(Connection conn, long id) throws SQLException{
		
		String result = "";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "Select W.md5 from service S, wsdl W where W.service_id = ? and S.id = W.service_id ;";
		
		pstmt = conn.prepareStatement(SQL);
		pstmt.setLong(1, id);
		rs = pstmt.executeQuery();
		
		// put result into classOBJs
		while (rs.next()) {
			result = rs.getString(1);
		}
		
		if (result.equals("")){
		    pstmt = null;
	        rs = null;
	        SQL = "Select SA.md5 from service S, sawsdl SA where SA.service_id = ? and S.id = W.service_id ;";
	        
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setLong(1, id);
	        rs = pstmt.executeQuery();
	        
	        // put result into classOBJs
	        while (rs.next()) {
	            result = rs.getString(1);
	        }
		}
		
		return result;
	}

	public static String getMD5(Connection conn, String name, String type) throws SQLException {

		String result = "";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "";
		if (type.equalsIgnoreCase("wsdl")){
		    SQL = "Select W.md5 from service S, wsdl W where S.name = ? and S.id = W.service_id ;";
		}else if (type.equalsIgnoreCase("sawsdl")){
		    SQL = "Select SA.md5 from service S, sawsdl SA where S.name = ? and S.id = SA.service_id ;";
		}
		
		pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, name);
		rs = pstmt.executeQuery();
		
		// put result into classOBJs
		while (rs.next()) {
			result = rs.getString(1);
		}
		
		return result;
	}


	public static String getWSDL(Connection conn, String name, String type) throws SQLException {
		
		String result = "";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "";
		if (type.equalsIgnoreCase("wsdl")){
		    SQL = "Select W.xml from Service S, wsdl W where S.name = ? and S.id = W.service_id ;";
		}else if (type.equalsIgnoreCase("sawsdl")){
		    SQL = "Select SA.xml from Service S, sawsdl SA where S.name = ? and S.id = SA.service_id ;";
		}
		pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, name);
		rs = pstmt.executeQuery();
		
		// put result into classOBJs
		while (rs.next()) {
			result = rs.getString(1);
		}
		return result;
		
	}


	public static ArrayList<String> getSAWSDLList(Connection connection) throws SQLException {
		
		ArrayList<String> list = new ArrayList<String>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "Select S.name from sawsdl SA, service S where SA.service_id = S.id;";
		
		pstmt = connection.prepareStatement(SQL);
		rs = pstmt.executeQuery();
		
		// put result into classOBJs
		while (rs.next()) {
			list.add(rs.getString(1));
		}
		
		return list;
		
	}


	public static ArrayList<String> getWSDLList(Connection connection) throws SQLException {
		
		ArrayList<String> list = new ArrayList<String>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "Select S.name from wsdl W, service S where W.service_id = S.id;";
		
		pstmt = connection.prepareStatement(SQL);
		rs = pstmt.executeQuery();
		
		// put result into classOBJs
		while (rs.next()) {
			list.add(rs.getString(1));
		}
		
		return list;
	}


	public static ArrayList<String> search(Connection conn, String searchWord) throws URISyntaxException, IOException, SQLException {

		String values = DataManager.tokenize(searchWord);
		
		ArrayList<String> result = new ArrayList<String>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "Select W.name, W.type, COUNT(*) as num " +
						"from keyword K, wsdl W " +
						"where " +
							"K.keyword IN (" + values + ") and K.id = W.id " +
						"group by K.id " +
						"order by num desc " +
						"limit 50 ; ";
		
		pstmt = conn.prepareStatement(SQL);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			result.add(rs.getString(1) + "." + rs.getString(2));
		}
		
		return result;
	
	}
	
	
	
}