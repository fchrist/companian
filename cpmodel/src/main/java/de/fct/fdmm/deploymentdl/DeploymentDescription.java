package de.fct.fdmm.deploymentdl;

import de.fct.fdmm.basis.FDObject;

public interface DeploymentDescription extends FDObject {

    public String getPackageFormatDescription();

    public String getInstallationDescription();
    
}
