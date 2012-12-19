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
