package de.fct.fdmm.sampledl;

import de.fct.fdmm.apidl.MethodDescription;

public interface HookElement extends DocElement {

    public MethodDescription getMethod();
    
    public void setMethod(MethodDescription method);
    
}
