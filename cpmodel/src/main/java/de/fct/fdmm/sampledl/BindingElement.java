package de.fct.fdmm.sampledl;

import de.fct.fdmm.bindingdl.BindingDescription;

public interface BindingElement extends DocElement {

    public BindingDescription getDescription();

    public void setDescription(BindingDescription description);

}
