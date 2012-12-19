package de.fct.companian.model.binding.umlpsc;

import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.UMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.model.binding.uml.UMLLoader;

public class UMLStateMachineLoader {
	
	private static Logger logger = LoggerFactory.getLogger(UMLStateMachineLoader.class);

	public static StateMachine loadStateMachine(URI modelUri) {
		org.eclipse.uml2.uml.Package model = UMLLoader.loadUMLModel(modelUri);
		
		StateMachine umlStateMachine = null;
		if (model != null) {
			umlStateMachine = (StateMachine)model.getPackagedElement(null, true, UMLPackage.Literals.STATE_MACHINE, false);
		}
		
		if (umlStateMachine != null && logger.isDebugEnabled()) {
			logger.debug("loadStateMachine() loaded state machine with owned elements " + umlStateMachine.getOwnedElements());
		}
		
		return umlStateMachine;
	}




	
}
