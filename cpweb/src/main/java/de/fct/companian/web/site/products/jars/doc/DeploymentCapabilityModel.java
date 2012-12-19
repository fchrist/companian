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
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.DeploymentCapability;

public class DeploymentCapabilityModel extends DocModel {

    private Logger logger = LoggerFactory.getLogger(DeploymentCapabilityModel.class);
    
    public DeploymentCapabilityModel(JarDao jarDao) {
        super(jarDao);
    }

    public DeploymentCapability refresh(FrameworkDescription fd, String deploymentName) {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh() binding " + deploymentName);
        }
        DeploymentCapability deploymentCapability = null;
        
        if (fd.getDeploymentCapabilities() != null && !fd.getDeploymentCapabilities().isEmpty()) {
            for (DeploymentCapability dc : fd.getDeploymentCapabilities()) {
                if (dc.getName().equals(deploymentName)) {
                    this.context.put("deployment", dc);
                    deploymentCapability = dc;
                    break;
                }
            }
        }
        
        return deploymentCapability;
    }

}
