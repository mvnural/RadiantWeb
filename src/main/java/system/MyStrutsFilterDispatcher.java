package system;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;

public class MyStrutsFilterDispatcher extends StrutsPrepareAndExecuteFilter {

	private static Set<String> terms = new HashSet<String>();
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		String url = ((HttpServletRequest) req).getRequestURI();
		if (checkTerm(url)){
			// struts2 filter
			super.doFilter(req, res, chain);
		}else if(url.indexOf(".action") > 0){
			// struts2 filter
			super.doFilter(req, res, chain);
		}else {
			// regular filter
			chain.doFilter(req, res);
		}
	}
	
	public boolean checkTerm(String url){
		/*
		if (terms.size() == 0){
			Struts2FilterConfig config = new Struts2FilterConfig();
			terms = config.getTerms();
		}
		for (String term : terms){
			if (url.indexOf(term) > 0){
				return true;
			}
		}
		*/
		return true;
	}

	public static void setTerms(Set<String> terms) {
		MyStrutsFilterDispatcher.terms = terms;
	}

	public static Set<String> getTerms() {
		return terms;
	}
	
}
