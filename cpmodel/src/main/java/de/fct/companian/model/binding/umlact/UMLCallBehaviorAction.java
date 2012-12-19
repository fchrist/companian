package de.fct.companian.model.binding.umlact;

import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.CallBehaviorAction;

import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.architecturedl.behaviordl.SubProcess;

public class UMLCallBehaviorAction extends UMLActivityNode implements SubProcess {

    public UMLCallBehaviorAction(UMLActivity umlActivity, CallBehaviorAction callBehaviorAction) {
        super(umlActivity, callBehaviorAction);
    }

    public Process getSubProcess() {
        CallBehaviorAction callBehaviorAction = (CallBehaviorAction)this.activityNode;
        Activity subActivity = (Activity)callBehaviorAction.getBehavior();
        return new UMLActivity(subActivity);
    }
 
    public String getType() {
        return "sub process";
    }
}
