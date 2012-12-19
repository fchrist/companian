package de.fct.companian.model.jel;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JEL {

    List<JelClass> jelclass;

    @XmlElement
    public List<JelClass> getJelclass() {
        return jelclass;
    }

    public void setJelclass(List<JelClass> jelclass) {
        this.jelclass = jelclass;
    }

}
