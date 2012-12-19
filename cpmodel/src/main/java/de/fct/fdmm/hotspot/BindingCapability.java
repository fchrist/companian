package de.fct.fdmm.hotspot;

import javax.xml.bind.annotation.XmlElement;

import de.fct.companian.model.binding.bdl.DefaultBindingDescription;
import de.fct.fdmm.basis.Description;
import de.fct.fdmm.bindingdl.BindingDescription;

public class BindingCapability extends Description {

    private BindingDescription bindingDescription;

    @XmlElement(type=DefaultBindingDescription.class)
    public BindingDescription getBindingDescription() {
        return bindingDescription;
    }

    public void setBindingDescription(BindingDescription bindingDescription) {
        this.bindingDescription = bindingDescription;
    }

}
