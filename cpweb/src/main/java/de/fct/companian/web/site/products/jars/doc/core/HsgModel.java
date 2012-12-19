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
package de.fct.companian.web.site.products.jars.doc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.web.site.products.jars.doc.DocModel;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;

public class HsgModel extends DocModel {

    private Logger logger = LoggerFactory.getLogger(HsgModel.class);
    
    public HsgModel(JarDao jarDao) {
        super(jarDao);
    }

    public HotSpotGroup refresh(FrameworkDescription fd, String hsgName) {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh() with HSG " + hsgName);
        }
        
        // search HSG and add to context
        HotSpotGroup hotSpotGroup = null;
        if (fd.getHotSpotGroups() != null
            && !fd.getHotSpotGroups().isEmpty()) {
            for (HotSpotGroup hsg : fd.getHotSpotGroups()) {
                if (hsg.getName().equals(hsgName)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("refresh() found hotspot group " + hsg.getName());
                    }
                    this.context.put("hsg", hsg);
                    hotSpotGroup = hsg;
                    break;
                }
            }
        }
        
        return hotSpotGroup;
    }
}
