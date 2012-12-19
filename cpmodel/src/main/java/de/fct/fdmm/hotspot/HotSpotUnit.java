package de.fct.fdmm.hotspot;

import javax.xml.bind.annotation.XmlAttribute;

import de.fct.fdmm.basis.Description;

public class HotSpotUnit extends Description {

    private HotSpotUnitKind kind;

    @XmlAttribute
    public HotSpotUnitKind getKind() {
        return kind;
    }

    public void setKind(HotSpotUnitKind kind) {
        this.kind = kind;
    }
    
    
}
