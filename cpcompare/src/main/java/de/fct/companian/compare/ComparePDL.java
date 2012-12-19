package de.fct.companian.compare;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.hotspot.HookProtocol;
import de.fct.fdmm.protocoldl.HookProtocolState;
import de.fct.fdmm.protocoldl.ProtocolDlProxy;
import de.fct.fdmm.protocoldl.ProtocolHook;
import de.fct.fdmm.protocoldl.StateMachine;

public class ComparePDL extends AbstractCompare {

    private static Logger logger = LoggerFactory.getLogger(ComparePDL.class);

    private ProtocolDlProxy proxy;

    public ComparePDL(Map<String,Object> context) {
        super(context);
    }

    protected FDifference compareHookProtocols(HookProtocol leftProtocol,
                                               HookProtocol rightProtocol) {

        FDifference fileDiff = null;
        FDifference smDiff = null;

        if (leftProtocol.getHookProtocolFile() != null) {
            if (rightProtocol.getHookProtocolFile() != null) {
                if (!leftProtocol.getHookProtocolFile().equals(rightProtocol.getHookProtocolFile())) {
                    // Compare the protocols - not matter if the file names are different
                    smDiff = this.compareStateMachines(leftProtocol, rightProtocol);
                }
            } else {
                // Deleted protocol
                fileDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                fileDiff.setSource(leftProtocol);
                fileDiff.setDescription("The Hook Protocol '" + leftProtocol.getName()
                                        + "' was removed in version " + this.getRightVersion());
            }
        } else if (rightProtocol.getHookProtocolFile() != null) {
            // Created protocol
            fileDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
            fileDiff.setSource(rightProtocol);
            fileDiff.setDescription("The Hook Protocol '" + rightProtocol.getName()
                                    + "' was added in version " + this.getRightVersion());
        }

        FDifference diff = null;
        if (fileDiff != null || smDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setSource(leftProtocol);
            diff.setDescription("The Hook Protocol '" + leftProtocol.getName() + "' has changed in version "
                                + this.getRightVersion());

            diff.addDifference(fileDiff);
            diff.addDifference(smDiff);
        }

        return diff;
    }

    private FDifference compareStateMachines(HookProtocol leftProtocol,
                                             HookProtocol rightProtocol) {
        logger.info("compareStateMachines() {} with {}", leftProtocol.getHookProtocolFile(), rightProtocol.getHookProtocolFile());
        
        StateMachine leftStateMachine = this.proxy.getStateMachine(leftProtocol.getHookProtocolFile());
        StateMachine rightStateMachine = this.proxy.getStateMachine(rightProtocol.getHookProtocolFile());

        logger.debug("compareStateMachines() left {} with right {}", leftStateMachine.toString(), rightStateMachine.toString());
        
        FDifference diff = null;

        HookProtocolState leftStart = leftStateMachine.getStartState();
        HookProtocolState rightStart = rightStateMachine.getStartState();

        if (leftStart != null) {
            if (rightStart != null) {
                diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
                bisimulate(leftStart, rightStart, null, diff);
            } else {
                // No right start state
                logger.warn("compareStateMachines() " + leftProtocol.getHookProtocolFile()
                            + ": could not find start state of right state machine - bisimulation cancelled");
            }
        } else if (rightStart != null) {
            // New start state
            logger.warn("compareStateMachines() " + rightProtocol.getHookProtocolFile()
                        + ": could not find start state of left state machine - bisimulation cancelled");
        }

        if (diff != null && !diff.getSubDiffs().isEmpty()) {
            diff.setSource(leftProtocol);
            diff.setDescription("The state machine defining the Hook Protocol '" + leftProtocol.getName()
                                + "' has changed in version " + this.getRightVersion());
        } else {
            diff = null;
        }

        return diff;
    }

    /**
     * Performs a bisimulation starting from the given left and right HookProtocolStates to determine
     * if both transition systems are equal.
     * 
     * @param leftState
     * @param rightState
     * @param visitedStateCouples
     * @param diff
     * @return
     */
    private void bisimulate(HookProtocolState leftState,
                            HookProtocolState rightState,
                            Set<StateCouple> visitedStateCouples,
                            FDifference diff) {
        
        logger.info("bisimulate() {} and {}", leftState.getId(), rightState.getId());
        
        if (visitedStateCouples == null) {
            visitedStateCouples = new HashSet<StateCouple>();
        }

        // If this couple was checked before, they are equivalent
        StateCouple couple = new StateCouple(leftState, rightState);
        if (visitedStateCouples.contains(couple)) {
            return;
        }
        else {
            visitedStateCouples.add(couple);
        }

        if (leftState.getId().equals(rightState.getId())) {
            bisimulateTransitions(leftState, rightState, visitedStateCouples, diff);
        }
        else {
            // The states differ according to their ID
            FDifference subDiff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
            subDiff.setSource(leftState);
            subDiff.setDescription("The Hook protocol state '" + leftState.getId()
                                   + "' was changed to '" + rightState.getId() + "' in version "
                                   + this.getRightVersion());
            diff.addDifference(subDiff);
        }
    }

    private void bisimulateTransitions(HookProtocolState leftState,
                                       HookProtocolState rightState,
                                       Set<StateCouple> visitedStateCouples,
                                       FDifference diff) {
        Set<ProtocolHook> corTransitions = new HashSet<ProtocolHook>(); 
        for (ProtocolHook leftHook : leftState.getSubsequentHooks()) {
            ProtocolHook corHook = filterTransitions(rightState, leftHook);
            if (corHook == null) {
                // Removed Hook
                FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                subDiff.setSource(leftState);
                subDiff.setDescription("The Hook '" + leftHook.getHookSignature()
                                       + "' was removed from state '" + leftState.getId() + "' in version "
                                       + this.getRightVersion());
                diff.addDifference(subDiff);
            }
            else {
                corTransitions.add(corHook);
            }
        }
        logger.info("bisimulate() found corresponding hooks {}", corTransitions);

        if (leftState.getSubsequentHooks().size() < rightState.getSubsequentHooks().size()) {
            // There are new transitions on the right side
            for (ProtocolHook rightHook : rightState.getSubsequentHooks()) {
                boolean found = false;
                for (ProtocolHook leftHook : leftState.getSubsequentHooks()) {
                    if (rightHook.getHookSignature().equals(leftHook.getHookSignature())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                    subDiff.setSource(leftState);
                    subDiff.setDescription("The Hook '" + rightHook.getHookSignature()
                                           + "' was added in state '" + leftState.getId() + "' in version "
                                           + this.getRightVersion());
                    diff.addDifference(subDiff);
                }
            }            
        }
        
        Iterator<ProtocolHook> correspondingSetIt = corTransitions.iterator();
        for (ProtocolHook leftHook : leftState.getSubsequentHooks()) {
            HookProtocolState leftTarget = leftHook.getTarget();

            // Check recursively the target states
            while (correspondingSetIt.hasNext()) {
                ProtocolHook correspondingHook = correspondingSetIt.next();
                HookProtocolState rightTarget = correspondingHook.getTarget();
                
                if (leftHook.getHookSignature().equals(correspondingHook.getHookSignature())) {
                    bisimulate(leftTarget, rightTarget, visitedStateCouples, diff);
                }
            }            
        }
    }

    private ProtocolHook filterTransitions(HookProtocolState state, ProtocolHook hook) {
        logger.info("filterTransitions() for hook {}", hook);

        for (ProtocolHook currentHook : state.getSubsequentHooks()) {
            if (currentHook.getHookSignature().equals(hook.getHookSignature())) {
                return currentHook;
            }
        }

        return null;
    }  

    public void setProxy(ProtocolDlProxy proxy) {
        this.proxy = proxy;
    }

}
