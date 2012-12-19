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
package de.fct.companian.web.site.products.jars.doc.hotspot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.web.site.products.jars.doc.core.HsgModel;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.hotspot.HotSpot;

public class HsModel extends HsgModel {

    private Logger logger = LoggerFactory.getLogger(HsModel.class);
    
    public HsModel(JarDao jarDao) {
        super(jarDao);
    }

    public HotSpot refresh(FrameworkDescription fd, String hsgName, String hsName) {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh() with Hotspot " + hsName);
        }
        HotSpotGroup hsg = super.refresh(fd, hsgName);
        HotSpot hotSpot = null;
        for (HotSpot hs : hsg.getHotSpots()) {
            if (hs.getName().equals(hsName)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("refresh() found hotspot " + hs.getName());
                }
                this.context.put("hs", hs);
                hotSpot = hs;
                break;
            }
        }
        
        return hotSpot;
    }

}
