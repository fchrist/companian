package de.fct.fdmm.bindingdl;

import de.fct.fdmm.basis.INamedElement;

public interface Task extends INamedElement {
    
    public String getSelector();
    
    public String getContent();
    
    public Descriptor getDescriptor();
}
