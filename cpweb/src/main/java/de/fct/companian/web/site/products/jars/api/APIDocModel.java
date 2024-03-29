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
package de.fct.companian.web.site.products.jars.api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.model.binding.jel.JelAPIDoc;
import de.fct.companian.model.jel.JEL;
import de.fct.companian.web.site.AbstractPageModel;
import de.fct.companian.web.site.products.jars.doc.DocModel;
import de.fct.fdmm.apidl.APIDoc;

public class APIDocModel extends AbstractPageModel {

    private Logger logger = LoggerFactory.getLogger(DocModel.class);
    
    private final JarDao jarDao;
    
    private APIDoc apidoc;
    
    public APIDocModel(JarDao jarDao) {
        this.jarDao = jarDao;
    }
    
    @Override
    public void init() {
    }

    public APIDoc refresh(int productId, int jarId) {
        Jar jar = this.jarDao.loadJar(jarId);
        this.context.put("jar", jar);
        this.context.put("product", jar.getProduct());
        if (jar != null) {
            this.context.put("title", "API Documentation of " + jar.getArtifact() + " " + jar.getVersion());
        }
        
        String jeldoc = this.jarDao.loadAPIDoc(jarId);
        if (jeldoc != null) {
            InputStream is;
            try {
                is = new ByteArrayInputStream(jeldoc.getBytes("UTF-8"));
                JEL jel = JAXB.unmarshal(is, JEL.class);
                this.apidoc = new JelAPIDoc(jel);
                this.context.put("api", this.apidoc);
                if (logger.isDebugEnabled()) {
                    logger.debug("refresh() loaded APIDoc -> JEL instance from DB");
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("refresh() error while refreshing APIDoc", e);
            }
        } else {
            this.apidoc = null;
            logger.error("refresh() no JEL doc available for JAR " + jarId);
        }
        
        return this.apidoc;
    }

}
