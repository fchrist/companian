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

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.bindingdl.MetaEntry;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.BindingCapability;

public class MetaEntryModel extends BindingCapabilityModel {

    public MetaEntryModel(JarDao jarDao) {
        super(jarDao);
    }

    public MetaEntry refresh(FrameworkDescription fd, String bindingName, String metaEntryName) {
        MetaEntry metaEntry = null;
        
        BindingCapability bc = super.refresh(fd, bindingName);
        if (bc != null && bc.getBindingDescription() != null) {
            if (bc.getBindingDescription().getMetaEntries() != null && !bc.getBindingDescription().getMetaEntries().isEmpty()) {
                for (MetaEntry me : bc.getBindingDescription().getMetaEntries()) {
                    if (me.getName().equals(metaEntryName)) {
                        metaEntry = me;
                        this.context.put("metaentry", me);
                        break;
                    }
                }
            }
        }
        
        return metaEntry;
    }

}
