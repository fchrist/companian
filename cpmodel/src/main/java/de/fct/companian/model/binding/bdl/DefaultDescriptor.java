package de.fct.companian.model.binding.bdl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.bindingdl.Descriptor;

public class DefaultDescriptor implements Descriptor {

    private String name;
    private String schemaUrn;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getSchemaUrn() {
        return schemaUrn;
    }

    public void setSchemaUrn(String schemaUrn) {
        this.schemaUrn = schemaUrn;
    }

}
