package edu.uga.radiant.ajax;

import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.util.RadiantToolConfig;

public class LoadSuggest extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the prefix character for matching
     */
	private String baseWord;
	
	/** response error message
     */
	private String errormsg;
	
	/** the collection for suggestion words
     */
	private TreeSet<String> suggestWords;
	
	/** for checking error
     */
	private boolean error = false;
	
	
	public String execute() {
		
		suggestWords = new TreeSet<String>();
		errormsg = "";
		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		OntologyManager mgr = (OntologyManager)session.get("OntologyManager");
		
		try {
			
			if (mgr != null){
				for (String word : mgr.getAllClassLabels().keySet()){
					if (word.contains(baseWord)){
						suggestWords.add(word);
					}
				}
			}else{
				//errormsg = "Error:ontology not loaded";
			}
			
			return SUCCESS; 
			
		}catch (Exception e) {
			e.printStackTrace();
			
			// record error log
			Logger logger = RadiantToolConfig.getLogger();
			logger.error(e.toString());
			
			return ERROR;
			
		}
		
	}
	
	public void setError(boolean error) {
		this.error = error;
	}
	
	public boolean isError() {
		return error;
	}

	public void setBaseWord(String baseWord) {
		this.baseWord = baseWord;
	}

	public String getBaseWord() {
		return baseWord;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setSuggestWords(TreeSet<String> suggestWords) {
		this.suggestWords = suggestWords;
	}

	public TreeSet<String> getSuggestWords() {
		return suggestWords;
	}

}
