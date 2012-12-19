package de.fct.companian.model.binding.umlact;

import org.eclipse.uml2.uml.InitialNode;

import de.fct.fdmm.architecturedl.behaviordl.StartStep;

public class UMLInitialNode extends UMLActivityNode implements StartStep {

    public UMLInitialNode(UMLActivity umlActivity, InitialNode initialNode) {
        super(umlActivity, initialNode);
    }

    public String getType() {
        return "start step";
    }
}
