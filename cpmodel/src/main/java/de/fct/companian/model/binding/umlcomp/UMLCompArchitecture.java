package de.fct.companian.model.binding.umlcomp;

import java.util.Set;

import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureElement;

public class UMLCompArchitecture implements ArchitectureDescription {

    private final Set<ArchitectureElement> subElements;
    
    public UMLCompArchitecture(Set<ArchitectureElement> subElements) {
        this.subElements = subElements;
    }
    
    public Set<ArchitectureElement> getSubElements() {
        return this.subElements;
    }

}
