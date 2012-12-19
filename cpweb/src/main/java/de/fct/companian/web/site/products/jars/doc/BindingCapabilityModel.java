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
package de.fct.companian.web.site.products.jars.doc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.bindingdl.MetaEntry;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.BindingCapability;

public class BindingCapabilityModel extends DocModel {

    private Logger logger = LoggerFactory.getLogger(BindingCapabilityModel.class);
    
    public BindingCapabilityModel(JarDao jarDao) {
        super(jarDao);
    }

    public BindingCapability refresh(FrameworkDescription fd, String bindingName) {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh() binding " + bindingName);
        }
        BindingCapability bindingCapability = null;
        
        if (fd.getBindingCapabilities() != null && !fd.getBindingCapabilities().isEmpty()) {
            for (BindingCapability bc : fd.getBindingCapabilities()) {
                if (bc.getName().equals(bindingName)) {
                    this.context.put("binding", bc);
                    bindingCapability = bc;
                    break;
                }
            }
        }
        
        return bindingCapability;
    }

}
