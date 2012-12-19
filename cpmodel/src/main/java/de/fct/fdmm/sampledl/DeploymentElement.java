package de.fct.fdmm.sampledl;

import de.fct.fdmm.deploymentdl.DeploymentDescription;

public interface DeploymentElement extends DocElement {

    public DeploymentDescription getDeployment();
    
    public void setDeployment(DeploymentDescription deployment);
    
}
