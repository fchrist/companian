package de.fct.companian.model.binding.umlact;

import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.PackageableElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.model.binding.uml.UMLLoader;
import de.fct.fdmm.architecturedl.behaviordl.BehaviorDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.Process;

public class UMLActBehaviorDlProxy implements BehaviorDlProxy {

    private static Logger logger = LoggerFactory.getLogger(UMLActBehaviorDlProxy.class);

    public Process getProcess(String processFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("getProcess() for " + processFile);
        }

        UMLActivity umlActivity = null;
        
        org.eclipse.emf.common.util.URI emfModelUri = org.eclipse.emf.common.util.URI.createURI(processFile);
        org.eclipse.uml2.uml.Package umlModel = UMLLoader.loadUMLModel(emfModelUri);

        if (umlModel.getPackagedElements() != null) {
            for (PackageableElement pe : umlModel.getPackagedElements()) {
                if (pe instanceof Activity) {
                    Activity activity = (Activity) pe;
                    umlActivity = new UMLActivity(activity);
                    break;
                }
            }
        }

        return umlActivity;
    }

}
