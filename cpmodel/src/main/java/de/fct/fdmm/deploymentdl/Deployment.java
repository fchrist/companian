package de.fct.fdmm.deploymentdl;

import de.fct.fdmm.basis.FDObject;

public interface Deployment extends FDObject {

    public String getPackageFormatDescription();

    public String getInstallationDescription();
    
}
