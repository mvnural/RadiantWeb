package edu.uga.radiantweb.freemarker;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import org.apache.struts2.ServletActionContext;

/**
 * Created by mnural on 12/8/13.
 */
public class ConfigurationFactory {
    private Configuration config;

    public ConfigurationFactory(){
        this.config = new Configuration();
        config.setServletContextForTemplateLoading(ServletActionContext.getServletContext(), "/templates");
        config.setObjectWrapper(new DefaultObjectWrapper());
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        config.setIncompatibleImprovements(new Version(2, 3, 20));
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }
}
