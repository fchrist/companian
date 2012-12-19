package de.fct.companian.model.binding.bdl;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.bindingdl.APICall;

public class DefaultAPICall implements APICall {

    private String description;
    private String methodAPIPath;
    
    @XmlElement
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public String getMethodAPIPath() {
        return this.methodAPIPath;
    }

    public void setMethodAPIPath(String methodAPIPath) {
        this.methodAPIPath = methodAPIPath;
    }

    public String getName() {
        return this.getMethodAPIPath();
    }
    
}
