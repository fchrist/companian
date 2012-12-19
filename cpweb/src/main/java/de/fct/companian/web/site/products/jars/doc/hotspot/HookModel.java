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

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.CodingUnit;
import de.fct.fdmm.hotspot.HookCall;
import de.fct.fdmm.hotspot.HotSpotUnit;
import de.fct.fdmm.hotspot.HotSpotUnitKind;

public class HookModel extends HsUnitModel {

    public HookModel(JarDao jarDao) {
        super(jarDao);
    }

    public HookCall refresh(FrameworkDescription fd,
                                     String hsgName,
                                     String hsName,
                                     String unitName,
                                     String methodAPIPath) {

        HookCall hookCall = null;
        
        HotSpotUnit hsu = super.refresh(fd, hsgName, hsName, unitName);
        if (hsu != null) {
            if (hsu.getKind() == HotSpotUnitKind.Coding) {
                CodingUnit cu = (CodingUnit)hsu;
                if (cu.getHooks() != null && !cu.getHooks().isEmpty()) {
                    for (HookCall hc : cu.getHooks()) {
                        if (hc.getMethodAPIPath().equals(methodAPIPath)) {
                            hookCall = hc;
                            this.context.put("hook", hc);
                            break;
                        }
                    }
                }
            }            
        }
        
        return hookCall;
    }

}
