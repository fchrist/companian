package de.fct.companian.model.jel;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class JelParams {

    private List<JelParam> param;

    @XmlElement
    public List<JelParam> getParam() {
        return param;
    }

    public void setParam(List<JelParam> param) {
        this.param = param;
    }
    
    
}
