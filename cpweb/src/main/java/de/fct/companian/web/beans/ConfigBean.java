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
package de.fct.companian.web.beans;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

public class ConfigBean implements ServletContextAware {
    
    private static Logger logger = LoggerFactory.getLogger(ConfigBean.class);
    
    // Internal paths
    private String contextPath = null;
    private String rootPath    = "";
    private String proxyUrl    = "/proxy?url=";

    public ConfigBean() {
        Properties config = new Properties();
        try {
            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");
            
            if (inStream != null) {
                config.load(inStream);
                
                logger.info("loaded configuration from config.properties");
            }
        } catch (Exception e) {
            logger.warn("Unable to load config.properties", e);
        }
    }
    
    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
    
    public String getRootPath() {
        return this.rootPath;
    }
    
    public void setServletContext(ServletContext servletContext) {
        if (this.contextPath == null) {
            String contextPath = servletContext.getRealPath("/");
            
            String separator = System.getProperty("file.separator");
            
            contextPath = contextPath.substring(0, contextPath.length() - 1);
            int idx = contextPath.lastIndexOf(separator);
            contextPath = contextPath.substring(idx + 1);
            
            if (contextPath.equals("winstoneEmbeddedWAR")) {
                contextPath = "";
            }
            
            this.contextPath = contextPath;
            if (logger.isInfoEnabled()) {
                logger.info("setServletContext() context path is " + contextPath);
            }
            
            this.updatePaths();
        }
    }
    
    private void updatePaths() {
        if (!this.contextPath.isEmpty()) {
            this.rootPath = "/" + this.contextPath;
        }

        this.proxyUrl = this.rootPath + "/proxy?url=";
    }
}
