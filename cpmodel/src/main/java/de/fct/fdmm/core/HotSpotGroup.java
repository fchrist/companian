package de.fct.fdmm.core;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.basis.Description;
import de.fct.fdmm.hotspot.HotSpot;

public class HotSpotGroup extends Description {

    private List<HotSpot> hotSpots;

    @XmlElement
    public List<HotSpot> getHotSpots() {
        return hotSpots;
    }

    public void setHotSpots(List<HotSpot> hotSpots) {
        this.hotSpots = hotSpots;
    }

}
