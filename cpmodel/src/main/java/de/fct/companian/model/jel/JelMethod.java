package de.fct.companian.model.jel;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class JelMethod {
    
    private List<JelComment> comment;
    private List<JelParams> params;

    private String visibility;
    private String fulltype;
    private String type;
    private String name;
    private Boolean jelAbstract;
    private String returncomment;
    private Boolean jelInterface;
    private Boolean jelStatic;
    private Boolean jelFinal;
    private Boolean jelSynchronized;
    private Boolean jelSynthetic;

    @XmlElement
    public List<JelComment> getComment() {
        return comment;
    }

    public void setComment(List<JelComment> comment) {
        this.comment = comment;
    }

    @XmlElement    
    public List<JelParams> getParams() {
        return params;
    }

    public void setParams(List<JelParams> params) {
        this.params = params;
    }

    @XmlAttribute
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @XmlAttribute
    public String getFulltype() {
        return fulltype;
    }

    public void setFulltype(String fulltype) {
        this.fulltype = fulltype;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "abstract")
    public Boolean getJelAbstract() {
        return jelAbstract;
    }

    public void setJelAbstract(Boolean jelAbstract) {
        this.jelAbstract = jelAbstract;
    }

    @XmlAttribute
    public String getReturncomment() {
        return returncomment;
    }

    public void setReturncomment(String returncomment) {
        this.returncomment = returncomment;
    }

    @XmlAttribute(name = "interface")
    public Boolean getJelInterface() {
        return jelInterface;
    }

    public void setJelInterface(Boolean jelInterface) {
        this.jelInterface = jelInterface;
    }

    @XmlAttribute(name = "static")
    public Boolean getJelStatic() {
        return jelStatic;
    }

    public void setJelStatic(Boolean jelStatic) {
        this.jelStatic = jelStatic;
    }
    
    @XmlAttribute(name = "final")
    public Boolean getJelFinal() {
        return jelFinal;
    }

    public void setJelFinal(Boolean jelFinal) {
        this.jelFinal = jelFinal;
    }

    @XmlAttribute(name = "synchronized")
    public Boolean getJelSynchronized() {
        return jelSynchronized;
    }

    public void setJelSynchronized(Boolean jelSynchronized) {
        this.jelSynchronized = jelSynchronized;
    }

    @XmlAttribute(name = "synthetic")
    public Boolean getJelSynthetic() {
        return jelSynthetic;
    }

    public void setJelSynthetic(Boolean jelSynthetic) {
        this.jelSynthetic = jelSynthetic;
    }

}
