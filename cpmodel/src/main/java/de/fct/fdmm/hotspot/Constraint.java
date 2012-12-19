package de.fct.fdmm.hotspot;

import javax.xml.bind.annotation.XmlElement;

import de.fct.companian.model.binding.cdl.DefaultAssertion;
import de.fct.fdmm.basis.Description;
import de.fct.fdmm.constraintdl.Assertion;

public class Constraint extends Description {

    private Assertion assertion;

    @XmlElement(type=DefaultAssertion.class)
    public Assertion getAssertion() {
        return assertion;
    }

    public void setAssertion(Assertion function) {
        this.assertion = function;
    }

    @Override
    public String getName() {
    	String name = super.getName();
    	if (name == null && this.assertion != null) {
    		name = this.assertion.getSignature();
    	}

    	return name;
    }
}
