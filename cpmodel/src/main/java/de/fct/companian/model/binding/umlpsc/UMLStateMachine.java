package de.fct.companian.model.binding.umlpsc;

import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.protocoldl.HookProtocolState;
import de.fct.fdmm.protocoldl.StateMachine;

public class UMLStateMachine implements StateMachine {

    private static Logger logger = LoggerFactory.getLogger(UMLStateMachine.class);
    
    private org.eclipse.uml2.uml.StateMachine uml2StateMachine;
    
    public UMLStateMachine(org.eclipse.uml2.uml.StateMachine uml2StateMachine) {
        this.uml2StateMachine = uml2StateMachine;
    }
    
    public HookProtocolState getStartState() {
        Pseudostate pseudoInit = retrieveInitState();
        UMLState startState = new UMLState(pseudoInit);

        return startState;
    }

    private Pseudostate retrieveInitState() {
        Pseudostate initState = null;
        for (Element smEl : this.uml2StateMachine.getOwnedElements()) {
            if (smEl instanceof Region) {
                Region region = (Region) smEl;
                if (logger.isDebugEnabled()) {
                    logger.debug("retrieveInitState() found region with owned elements "
                                 + region.getOwnedElements());
                }
                for (Element regEl : region.getOwnedElements()) {
                    if (regEl instanceof Pseudostate) {
                        Pseudostate ps = (Pseudostate) regEl;
                        if (logger.isDebugEnabled()) {
                            logger.debug("retrieveInitState() found pseudostate " + ps);
                        }
                        if (ps.getKind() == PseudostateKind.INITIAL_LITERAL) {
                            initState = ps;
                            if (logger.isDebugEnabled()) {
                                logger.debug("retrieveInitState() found INIT pseudostate " + initState);
                            }
                            break;
                        }
                    }
                }
            }
            if (initState != null) {
                break;
            }
        }

        return initState;
    }
    
    @Override
    public String toString() {
        return this.uml2StateMachine.toString();
    }
}
