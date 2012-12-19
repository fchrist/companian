package de.fct.fdmm.architecturedl.behaviordl;

import java.util.Set;

import de.fct.fdmm.basis.INamedElement;

public interface Process extends INamedElement  {

    public Set<Step> getSteps();
}
