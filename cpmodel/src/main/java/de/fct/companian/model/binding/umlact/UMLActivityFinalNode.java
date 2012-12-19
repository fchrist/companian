package de.fct.companian.model.binding.umlact;

import org.eclipse.uml2.uml.ActivityFinalNode;

import de.fct.fdmm.architecturedl.behaviordl.EndStep;


public class UMLActivityFinalNode extends UMLActivityNode implements EndStep {

    public UMLActivityFinalNode(UMLActivity umlActivity, ActivityFinalNode activityFinalNode) {
        super(umlActivity, activityFinalNode);
    }

    public String getType() {
        return "end step";
    }
}
