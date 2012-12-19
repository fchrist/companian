package de.fct.fdmm.constraintdl;

import java.util.List;

import de.fct.fdmm.basis.INamedElement;

public interface Assertion extends INamedElement {

    public List<String> getParameters();

    public String getSignature();
}
