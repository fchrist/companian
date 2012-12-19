/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package de.fct.companian.web.site;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.web.beans.ConfigBean;

public abstract class AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(AbstractPageRenderer.class);

    protected VelocityEngine velocityEngine;
    
    protected ConfigBean configBean;
    
    protected AbstractPageModel pageModel;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getPage() {
        if (logger.isDebugEnabled()) {
            logger.debug("getPage() getting page by default implementation");
        }
        if (this.pageModel != null && this.pageModel.getContext() != null) {
            return this.renderPage(this.pageModel.getContext(), this.generateTemplateName());
        }
        else {
            return this.renderPage(null, this.generateTemplateName());
        }
    }
        
    protected String renderPage(VelocityContext context, String templateName) {
        if (logger.isInfoEnabled()) {
            logger.info("renderPage() " + templateName);
        }
        VelocityContext rootContext = getRootContext(context);
        
        String errorMsg = null;
        try {
            Template t = this.velocityEngine.getTemplate(templateName, "UTF-8");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");

            t.merge(rootContext, writer);
            writer.flush();
            return out.toString();
        } catch (ResourceNotFoundException e) {
            logger.error("renderTemplate() resource not found", e);
            errorMsg = e.getMessage();
        } catch (ParseErrorException e) {
            logger.error("renderTemplate() parse error", e);
            errorMsg = e.getMessage();
        } catch (MethodInvocationException e) {
            logger.error("renderTemplate() invocation error", e);
            errorMsg = e.getMessage();
        } catch (IOException e) {
            logger.error("renderTemplate() io error", e);
            errorMsg = e.getMessage();
        } catch (Exception e) {
            logger.error("renderTemplate() error", e);
            errorMsg = e.getMessage();
        }

        if (errorMsg != null) {
            logger.error("renderTemplate() " + errorMsg);
        }
        return errorMsg;
    }

    private VelocityContext getRootContext(VelocityContext context) {
        VelocityContext rootContext;
        if (context != null) {
            rootContext = new VelocityContext(context);
        }
        else {
            rootContext = new VelocityContext();
        }
       
        rootContext.put("config", this.configBean);
        rootContext.put("version", extractVersionInfo());
        rootContext.put("navigation", this.getNavigation());
        
        return rootContext;
    }

    private String generateTemplateName() {
        // Generate template name from class file name
        String templateName = this.getClass().getName();
        templateName = templateName.replaceAll("\\.", "/");
        templateName = templateName + ".htm";
        templateName = "/" + templateName;

        if (logger.isDebugEnabled()) {
            logger.debug("generateTemplateName() returns " + templateName);
        }
        return templateName;
    }
    
    protected String getTemplate(String templateName) {
        String path = this.generateTemplatePath();
        if (!templateName.endsWith(".htm")) {
            templateName += ".htm";
        }
        return path + "/" + templateName;
    }
    
    private String generateTemplatePath() {
        // Generate template name from class file name
        String templatePath = this.getClass().getName();
        templatePath = templatePath.substring(0, templatePath.lastIndexOf('.'));
        templatePath = templatePath.replaceAll("\\.", "/");
        templatePath = "/" + templatePath;

        if (logger.isDebugEnabled()) {
            logger.debug("generateTemplatePath() returns " + templatePath);
        }
        return templatePath;
    }
    
    protected abstract List<String> getNavigation();
    
    protected List<String> getDefaultNavigation() {
      List<String> navigationList = new ArrayList<String>();
      navigationList.add(this.createNavLink("/", "homepage"));
      return navigationList;
    }
    
    protected String createNavLink(String subpath, String label) {
        String link = "<a href='" + this.configBean.getRootPath() + "/site" + subpath + "'>" + label
                      + "</a>";
        return link;
    }    
    
//    private List<String> generatePathSegments() {
//        List<String> navigationList = new ArrayList<String>();
//        navigationList.add("<a href='" + this.configBean.getRootPath() + "/site'>homepage</a>");
//
//        String path = this.getClass().getName();
//        path = path.replace("de.fct.companian.web.site.", "");
//        int idx = path.lastIndexOf('.');
//        if (idx > -1) {
//            path = path.substring(0, idx);
//            
//            String [] segments = path.split("\\.");
//            
//            path = this.configBean.getRootPath() + "/site";
//            for (int i=0; i<segments.length; i++) {
//                path += "/" + segments[i];
//                navigationList.add("<a href='" + path + "'>" + segments[i] + "</a>");
//            }
//        }
//        
//        return navigationList;
//    }
    
    private Object extractVersionInfo() {
        Properties properties = new Properties();
        try {
            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("version.properties");
            properties.load(inStream);
        } catch (Exception e) {
            logger.warn("Unable to load version.properties", e);
        }
        String pomVersion = properties.getProperty("pom.version");
        if (pomVersion.equalsIgnoreCase("${pom.version}")) {
            pomVersion = "Development";
        }
        return pomVersion;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
    
    public void setConfigBean(ConfigBean configBean) {
        this.configBean = configBean;
    }

    public AbstractPageModel getPageModel() {
        return pageModel;
    }

    public void setPageModel(AbstractPageModel model) {
        this.pageModel = model;
    }

}
