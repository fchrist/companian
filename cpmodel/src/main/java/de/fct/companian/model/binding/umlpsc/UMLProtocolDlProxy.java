package de.fct.companian.model.binding.umlpsc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.protocoldl.ProtocolDlProxy;
import de.fct.fdmm.protocoldl.StateMachine;

public class UMLProtocolDlProxy implements ProtocolDlProxy {

    private static Logger logger = LoggerFactory.getLogger(UMLProtocolDlProxy.class);

    public StateMachine getStateMachine(String hookProtocolFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("getStateMachine() for " + hookProtocolFile);
        }
        org.eclipse.emf.common.util.URI emfModelUri = org.eclipse.emf.common.util.URI
                .createURI(hookProtocolFile);
        org.eclipse.uml2.uml.StateMachine uml2StateMachine = UMLStateMachineLoader
                .loadStateMachine(emfModelUri);

        return new UMLStateMachine(uml2StateMachine);
    }

}
