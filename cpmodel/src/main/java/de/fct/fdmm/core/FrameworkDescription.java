package de.fct.fdmm.core;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.fct.fdmm.basis.Description;
import de.fct.fdmm.hotspot.BindingCapability;
import de.fct.fdmm.hotspot.DeploymentCapability;

@XmlRootElement
public class FrameworkDescription extends Description {

    private List<StructureView> structureViews;

    private List<BehaviorView> behaviorViews;
    
    private List<HotSpotGroup> hotSpotGroups;
    
    private List<BindingCapability> bindingCapabilities;
    
    private List<DeploymentCapability> deploymentCapabilities;

    @XmlElement
    public List<StructureView> getStructureViews() {
        return structureViews;
    }

    public void setStructureViews(List<StructureView> archViews) {
        this.structureViews = archViews;
    }

    @XmlElement
    public List<BehaviorView> getBehaviorViews() {
        return behaviorViews;
    }

    public void setBehaviorViews(List<BehaviorView> behaviorViews) {
        this.behaviorViews = behaviorViews;
    }

    @XmlElement
    public List<HotSpotGroup> getHotSpotGroups() {
        return hotSpotGroups;
    }

    public void setHotSpotGroups(List<HotSpotGroup> hotSpotGroups) {
        this.hotSpotGroups = hotSpotGroups;
    }

    @XmlElement
    public List<BindingCapability> getBindingCapabilities() {
        return bindingCapabilities;
    }

    public void setBindingCapabilities(List<BindingCapability> bindingCapabilities) {
        this.bindingCapabilities = bindingCapabilities;
    }

    public List<DeploymentCapability> getDeploymentCapabilities() {
        return deploymentCapabilities;
    }

    public void setDeploymentCapabilities(List<DeploymentCapability> deploymentCapabilities) {
        this.deploymentCapabilities = deploymentCapabilities;
    }
    
}
