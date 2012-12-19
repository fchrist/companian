package de.fct.fdmm.core;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.architecturedl.behaviordl.BehaviorDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.basis.Description;

public class BehaviorView extends Description {

    protected String processFile;
    
    @XmlElement
    public String getProcessFile() {
        return processFile;
    }

    public void setProcessFile(String processFile) {
        this.processFile = processFile;
    }

    public Process getProcess(BehaviorDlProxy proxy) {
        return proxy.getProcess(processFile);
    }
}
