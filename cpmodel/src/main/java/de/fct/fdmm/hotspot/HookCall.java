package de.fct.fdmm.hotspot;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.apidl.APIDlProxy;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.basis.INamedElement;

public class HookCall implements INamedElement {

    private String methodAPIPath;
    private List<Constraint> constraints;
    
    @XmlElement
    public String getMethodAPIPath() {
        return methodAPIPath;
    }

    public void setMethodAPIPath(String methodUri) {
        this.methodAPIPath = methodUri;
    }
    
    public String getSignature() {
        int lastSlash = this.methodAPIPath.lastIndexOf('/');
        String signature = this.methodAPIPath.substring(lastSlash+1);
        return signature;
    }

    public MethodDescription getMethod(APIDlProxy proxy) {
        return proxy.getMethodDescription(this.methodAPIPath);
    }

    @XmlElement
    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public String getName() {
        return this.getSignature();
    }
    
    public String toString() {
    	return this.getSignature();
    }

}
