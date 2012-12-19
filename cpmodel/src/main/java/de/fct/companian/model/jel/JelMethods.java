package de.fct.companian.model.jel;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class JelMethods {

    private List<JelMethod> method;

    @XmlElement
    public List<JelMethod> getMethod() {
        return method;
    }

    public void setMethod(List<JelMethod> method) {
        this.method = method;
    }
    
}
