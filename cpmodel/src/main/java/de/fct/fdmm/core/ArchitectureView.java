package de.fct.fdmm.core;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureDlProxy;
import de.fct.fdmm.basis.Description;

public abstract class ArchitectureView extends Description {

    protected String architectureDescriptionFile;
    
    @XmlElement
    public String getArchitectureDescriptionFile() {
        return architectureDescriptionFile;
    }

    public void setArchitectureDescriptionFile(String architectureDescriptionFile) {
        this.architectureDescriptionFile = architectureDescriptionFile;
    }

    public ArchitectureDescription getArchitectureDescription(ArchitectureDlProxy proxy) {
        return proxy.getArchitectureDescription(architectureDescriptionFile);
    }
}
