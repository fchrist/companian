package de.fct.companian.model.jel;

import javax.xml.bind.annotation.XmlElement;

public class JelComment {

    private String description;

    @XmlElement
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
