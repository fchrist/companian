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
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;

public class HsUnitModel extends HsModel {

    private Logger logger = LoggerFactory.getLogger(HsUnitModel.class);

    public HsUnitModel(JarDao jarDao) {
        super(jarDao);
    }

    public HotSpotUnit refresh(FrameworkDescription fd, String hsgName, String hsName, String unitName) {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh() with hotspot unit " + unitName);
        }
        HotSpot hs = super.refresh(fd, hsgName, hsName);
        HotSpotUnit hotSpotUnit = null;
        for (HotSpotUnit hsu : hs.getUnits()) {
            if (hsu.getName().equals(unitName)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("refresh() found hotspot unit " + hsu.getName());
                }                
                this.context.put("hsu", hsu);
                hotSpotUnit = hsu;
                
                break;
            }
        }
        
        return hotSpotUnit;
    }

}
