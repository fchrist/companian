package de.fct.companian.model.jel;

import javax.xml.bind.annotation.XmlAttribute;

public class JelParam {

    private String name;
    private String fulltype;
    private String type;
    private String comment;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
