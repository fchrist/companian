package de.fct.companian.use;

import java.util.ArrayList;
import java.util.List;

import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;

public class UseResult {

    private HotSpotGroup hotSpotGroup;
    private HotSpot hotSpot;
    private HotSpotUnit hotSpotUnit;
    private List<ClassUseConfidence> confidenceList;

    public UseResult() {};
    
    public UseResult(HotSpotGroup hotSpotGroup, HotSpot hotSpot, HotSpotUnit hotSpotUnit, List<ClassUseConfidence> confidenceList) {
        this.hotSpotGroup = hotSpotGroup;
        this.hotSpot = hotSpot;
        this.hotSpotUnit = hotSpotUnit;
        this.confidenceList = confidenceList;
    }
    
    public UseResult(HotSpotGroup hotSpotGroup, HotSpot hotSpot, HotSpotUnit hotSpotUnit, ClassUseConfidence confidence) {
        this.hotSpotGroup = hotSpotGroup;
        this.hotSpot = hotSpot;
        this.hotSpotUnit = hotSpotUnit;
        this.confidenceList = new ArrayList<ClassUseConfidence>();
        this.confidenceList.add(confidence);
    }

    public HotSpotGroup getHotSpotGroup() {
        return hotSpotGroup;
    }

    public void setHotSpotGroup(HotSpotGroup hotSpotGroup) {
        this.hotSpotGroup = hotSpotGroup;
    }

    public HotSpot getHotSpot() {
        return hotSpot;
    }

    public void setHotSpot(HotSpot hotSpot) {
        this.hotSpot = hotSpot;
    }

    public HotSpotUnit getHotSpotUnit() {
        return hotSpotUnit;
    }

    public void setHotSpotUnit(HotSpotUnit hotSpotUnit) {
        this.hotSpotUnit = hotSpotUnit;
    }

    public List<ClassUseConfidence> getConfidenceList() {
        return confidenceList;
    }

    public void setConfidenceList(List<ClassUseConfidence> confidenceList) {
        this.confidenceList = confidenceList;
    }

}
