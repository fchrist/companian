package de.fct.fdmm.architecturedl;

import java.util.Set;

import de.fct.fdmm.basis.FDObject;


public interface ArchitectureDescription extends FDObject {

    public Set<ArchitectureElement> getSubElements();

}
