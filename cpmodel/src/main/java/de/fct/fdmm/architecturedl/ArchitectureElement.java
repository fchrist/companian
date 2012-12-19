package de.fct.fdmm.architecturedl;

import java.util.Set;

import de.fct.fdmm.basis.INamedElement;

public interface ArchitectureElement extends INamedElement {
    
    public ArchitectureDescription getSubArchitecture();

    public Set<ArchitectureElement> getDependencies();
    
    public Set<String> getHotSpotGroups();

}
