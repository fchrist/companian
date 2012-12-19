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

import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.web.BehaviorDlSerializer;
import de.fct.fdmm.architecturedl.behaviordl.BehaviorDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.core.BehaviorView;
import de.fct.fdmm.core.FrameworkDescription;

public class BehaviorViewModel extends DocModel {

    private Logger logger = LoggerFactory.getLogger(BehaviorViewModel.class);

    private BehaviorDlProxy proxy;
    
    public BehaviorViewModel(JarDao jarDao) {
        super(jarDao);
    }

    @Override
    public void init() {}

    public BehaviorView refresh(FrameworkDescription fd, String behaviorViewName) {
        BehaviorView behaviorView = null;
        if (fd != null && behaviorViewName != null) {
            if (fd.getBehaviorViews() != null && !fd.getBehaviorViews().isEmpty()) {
                for (BehaviorView bv : fd.getBehaviorViews()) {
                    if (bv.getName().equals(behaviorViewName)) {
                        this.context.put("behaviorView", bv);
                        behaviorView = bv;
                        break;
                    }
                }
            }
        }
        
        return behaviorView;
    }

    public Process refresh(BehaviorView bv) {
        Process process = null;
        if (bv.getProcessFile() != null) {
            process = bv.getProcess(this.proxy);
            if (process != null) {
                try {
                    this.context.put("architectureAsJSON", BehaviorDlSerializer.toJSON(process).toString(2));
                } catch (JSONException e) {
                    logger.error("refresh() error while serializing architecture to JSON", e);
                }
            }
        }
        
        return process;
    }

    public void setProxy(BehaviorDlProxy proxy) {
        this.proxy = proxy;
    }
}
