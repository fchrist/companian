package de.fct.fdmm.apidl;

import de.fct.fdmm.basis.INamedElement;

public interface MethodDescription extends INamedElement {

    public MethodSignature getSignature();

    public TypeDescription getDefiningType();

}
