package de.fct.companian.model.binding.ddl;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.deploymentdl.DeploymentDescription;

public class DefaultDeployment implements DeploymentDescription {

    private String packageFormatDescription;
    
    private String installationDescription;

    @XmlElement
    public String getPackageFormatDescription() {
        return packageFormatDescription;
    }

    public void setPackageFormatDescription(String packageFormatDescription) {
        this.packageFormatDescription = packageFormatDescription;
    }

    @XmlElement
    public String getInstallationDescription() {
        return installationDescription;
    }

    public void setInstallationDescription(String installationDescription) {
        this.installationDescription = installationDescription;
    }

}
