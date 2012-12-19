package de.fct.fdmm.hotspot;

import javax.xml.bind.annotation.XmlElement;

import de.fct.companian.model.binding.ddl.DefaultDeployment;
import de.fct.fdmm.basis.Description;
import de.fct.fdmm.deploymentdl.DeploymentDescription;

public class DeploymentCapability extends Description {

    private DeploymentDescription deploymentDescription;

    @XmlElement(type=DefaultDeployment.class)
    public DeploymentDescription getDeploymentDescription() {
        return deploymentDescription;
    }

    public void setDeploymentDescription(DeploymentDescription deployment) {
        this.deploymentDescription = deployment;
    }
}
