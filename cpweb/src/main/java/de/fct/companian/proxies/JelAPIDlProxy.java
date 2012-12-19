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
package de.fct.companian.proxies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.web.site.products.jars.api.MethodModel;
import de.fct.fdmm.apidl.APIDlProxy;
import de.fct.fdmm.apidl.APIDoc;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.apidl.TypeDescription;

public class JelAPIDlProxy implements APIDlProxy {

    private static Logger logger = LoggerFactory.getLogger(JelAPIDlProxy.class);
    
    private final MethodModel model;
    
    public JelAPIDlProxy(MethodModel model) {
        this.model = model;
    }
    
    public APIDoc getAPIDoc(String apiDocPath) {
        if (logger.isDebugEnabled()) {
            logger.debug("getAPIDoc() for " + apiDocPath);
        }

        if (apiDocPath != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("getAPIDoc() getting values from path " + apiDocPath);
            }
            
            String [] pathsegments = apiDocPath.split("/");
            
            try {
                int productId = Integer.valueOf(pathsegments[3]).intValue();
                int jarId = Integer.valueOf(pathsegments[5]).intValue();
                
                return this.model.refresh(productId, jarId);                
            }
            catch (Throwable t) {
                if (logger.isDebugEnabled()) {
                    logger.debug("getAPIDoc() could not parse required information from path " + apiDocPath);
                }
                return null;
            }
        }
        else {
            return null;
        }
    }

    public MethodDescription getMethodDescription(String methodAPIPath) {
        if (logger.isDebugEnabled()) {
            logger.debug("getMethodDescription() for " + methodAPIPath);
        }

        if (methodAPIPath != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("getMethodDescription() getting values from path " + methodAPIPath);
            }
            
            String [] pathsegments = methodAPIPath.split("/");
            
            try {
                int productId = Integer.valueOf(pathsegments[3]).intValue();
                int jarId = Integer.valueOf(pathsegments[5]).intValue();
                String packageName = pathsegments[7];
                String typeName = pathsegments[8];
                String signature = pathsegments[9];
                
                return this.model.refresh(productId, jarId, packageName, typeName, signature);                
            }
            catch (Throwable t) {
                if (logger.isDebugEnabled()) {
                    logger.debug("getMethodDescription() could not parse required information from path " + methodAPIPath);
                }
                return null;
            }
        }
        else {
            return null;
        }
    }

    public TypeDescription getTypeDescription(String typeAPIPath) {
        if (logger.isDebugEnabled()) {
            logger.debug("getTypDescription() for " + typeAPIPath);
        }

        if (typeAPIPath != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("getTypDescription() getting values from path " + typeAPIPath);
            }
            
            String [] pathsegments = typeAPIPath.split("/");
            
            try {
                int productId = Integer.valueOf(pathsegments[3]).intValue();
                int jarId = Integer.valueOf(pathsegments[5]).intValue();
                String packageName = pathsegments[7];
                String typeName = pathsegments[8];
                
                return this.model.refresh(productId, jarId, packageName, typeName);                
            }
            catch (Throwable t) {
                if (logger.isDebugEnabled()) {
                    logger.debug("getTypDescription() could not parse required information from path " + typeAPIPath);
                }
                return null;
            }
        }
        else {
            return null;
        }
    }

}
