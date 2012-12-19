package de.fct.fdmm.architecturedl.behaviordl;

import de.fct.fdmm.basis.INamedElement;

public interface Step extends INamedElement {

    public Process getProcess();
    
    public Step getNextStep();
    public Step getPrevStep();

    public String getType();
    
}
