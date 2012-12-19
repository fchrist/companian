package de.fct.fdmm.hotspot;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.basis.INamedElement;
import de.fct.fdmm.protocoldl.ProtocolDlProxy;
import de.fct.fdmm.protocoldl.StateMachine;

public class HookProtocol implements INamedElement {

    private String hookProtocolFile;

    @XmlElement
    public String getHookProtocolFile() {
        return hookProtocolFile;
    }

    public void setHookProtocolFile(String stateMachinePath) {
        this.hookProtocolFile = stateMachinePath;
    }

    public StateMachine getStateMachine(ProtocolDlProxy protocolDlProxy) {
        return protocolDlProxy.getStateMachine(this.hookProtocolFile);
    }

    public String getName() {
        int lastSlash = this.hookProtocolFile.lastIndexOf('/');
        return this.hookProtocolFile.substring(lastSlash + 1);
    }

}
