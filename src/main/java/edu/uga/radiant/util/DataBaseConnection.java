package edu.uga.radiant.util;

import java.sql.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataBaseConnection {

	private Connection conn = null;

	public DataBaseConnection() {
		
		try {
			
			InitialContext ctx = new InitialContext();
			new RadiantToolConfig();
            DataSource ds = (DataSource)ctx.lookup(RadiantToolConfig.getEnv());
            
			conn = ds.getConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}

	public Connection getConnection() {
		return this.conn;
	}

	public void close() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
	
