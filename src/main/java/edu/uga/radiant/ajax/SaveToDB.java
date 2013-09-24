package edu.uga.radiant.ajax;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.lumina.discovery.util.WSDLSystem;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.radiant.util.DataBaseConnection;
import edu.uga.radiant.util.DataManager;
import edu.uga.radiant.util.QueryManager;
import edu.uga.radiant.util.RadiantToolConfig;

public class SaveToDB extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String errormsg;
	
	
	public String execute() {
		
		errormsg = "";
		DataBaseConnection dbcn = new DataBaseConnection();
		Connection conn = dbcn.getConnection();
		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		SAWSDLParser wsparser = (SAWSDLParser)session.get("wsdlparser");
		String filename = (String) session.get("wsname");
		
		try {
			
			String xml = wsparser.updateToXml();
			String md5 = WSDLSystem.generateMD5(xml);
			String name = "";
			if (filename.split(".").length > 1) name = filename.split(".")[0];
			
			//String oldMD5 = QueryManager.getMD5(conn, name, "sawsdl");
			//if (oldMD5.equals("")){
			//	DataManager.storeWSDL(conn, name, "sawsdl", xml, md5);	
			//}else if (!oldMD5.equals(md5)){
			//	long id = QueryManager.getWSDLID(conn, name, "sawsdl");
			//	DataManager.updateWSDL(conn, id, xml, oldMD5);
			//}
			
			return SUCCESS; 
			
		}catch (Exception e) {
			e.printStackTrace();
			errormsg = e.toString();
			// record error log
			Logger logger = RadiantToolConfig.getLogger();
			logger.error(e.toString());
			return ERROR;
		}
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

}

