package de.fct.fdmm.bindingdl;

import de.fct.fdmm.basis.IDescription;
import de.fct.fdmm.basis.INamedElement;

public interface APICall extends INamedElement, IDescription {

    public String getMethodAPIPath();

}
