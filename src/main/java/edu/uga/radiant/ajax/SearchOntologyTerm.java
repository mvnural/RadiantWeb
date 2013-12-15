package edu.uga.radiant.ajax;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import com.opensymphony.xwork2.inject.Inject;
import edu.uga.radiantweb.freemarker.ConfigurationFactory;
import edu.uga.radiantweb.freemarker.model.OntologySearchResult;
import freemarker.template.*;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.semanticweb.owlapi.model.OWLClass;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.ontology.SearchOntology;
import edu.uga.radiant.printTree.LoadOWLTree;
import edu.uga.radiant.util.RadiantToolConfig;
import edu.uga.radiant.util.SortValueMap;

public class SearchOntologyTerm extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String term;
	private String errormsg;
	private String innerHtml;

    @Inject("freemarkerConfiguration")
    private ConfigurationFactory freemarkerConfig;
	
	public String execute() {
		Logger logger = RadiantToolConfig.getLogger();
		errormsg = "";
		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		OntologyManager mgr = (OntologyManager) session.get("OntologyManager");
		
		StringWriter buf = new StringWriter();
		SearchOntology search = new SearchOntology(mgr);


        /*Create data model for freemarker template*/
        Map templateModel = new HashMap();
        List<OntologySearchResult> searchResults = new ArrayList<OntologySearchResult>();
        templateModel.put("searchResults", searchResults);


        logger.debug("term = " + term);
        SortValueMap<String, Double> searchTerms = search.search(term);
        logger.debug("searchTerms size = " + searchTerms.size());

        Iterator<String> it = searchTerms.keySet().iterator();
        int i = 0;
        while(it.hasNext() && i < 20){
            String iri = it.next();
            OWLClass cls = mgr.getConceptClass(iri);
            OntologySearchResult result = new OntologySearchResult();
            result.setDefinition(mgr.getClassDefinition(cls));
            result.setLabel(mgr.getClassLabel(cls));
            result.setFragmentData(LoadOWLTree.charReplace(iri));
            String score = searchTerms.get(iri).toString();
            if (score.length() > 6){
                score = score.substring(0, 6);
            }

            result.setScore(score);
            searchResults.add(result);

            i++;
        }
        try {
            Template temp = freemarkerConfig.getConfig().getTemplate("OntologySearchResults.ftl");
            temp.process(templateModel, buf);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        innerHtml = buf.toString();
	    
	    return SUCCESS;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setInnerHtml(String innerHtml) {
		this.innerHtml = innerHtml;
	}

	public String getInnerHtml() {
		return innerHtml;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getTerm() {
		return term;
	}

}
