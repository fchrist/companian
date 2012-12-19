package de.fct.companian.model.binding.umlact;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.OpaqueAction;

import de.fct.fdmm.architecturedl.behaviordl.HotSpotGroupCall;

public class UMLOpaqueAction extends UMLActivityNode implements HotSpotGroupCall {

    public UMLOpaqueAction(UMLActivity umlActivity, OpaqueAction opaqueAction) {
        super(umlActivity, opaqueAction);
    }

    public Set<String> getHotSpotGroups() {
        Set<String> hotSpotGroups = new HashSet<String>();
        OpaqueAction opaqueAction = (OpaqueAction)this.activityNode;
        EList<String> bodies = opaqueAction.getBodies();
        if (bodies != null && bodies.size() > 1) {
            String body = bodies.get(0);
            if (body.contains("HotSpotGroups")) {
                for (int i=1; i<bodies.size(); i++) {
                    hotSpotGroups.add(bodies.get(i));
                }
            }
        }
        
        return hotSpotGroups;
    }

    public String getType() {
        return "hotspot group call";
    }
}
