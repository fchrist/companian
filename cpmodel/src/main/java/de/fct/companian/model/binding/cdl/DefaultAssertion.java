package de.fct.companian.model.binding.cdl;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.constraintdl.Assertion;

public class DefaultAssertion implements Assertion {
    
    private String name;
    private List<String> parameters;

    @XmlAttribute
    public String getName() {
        return this.name;
    }
    
    @XmlElement
    public List<String> getParameters() {
        return this.parameters;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
    
    public String getSignature() {
        String pl = this.name + "(";
        if (this.parameters != null && !this.parameters.isEmpty()) {
            for (int i=0; i<this.parameters.size(); i++) {
                if (i > 0) {
                    pl += ", ";
                }
                pl += this.parameters.get(i);
            }
        }
        pl += ")";
        return pl;
    }

}
