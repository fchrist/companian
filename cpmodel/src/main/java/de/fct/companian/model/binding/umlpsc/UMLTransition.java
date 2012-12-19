package de.fct.companian.model.binding.umlpsc;

import org.eclipse.uml2.uml.CreationEvent;
import org.eclipse.uml2.uml.DestructionEvent;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.MessageEvent;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.hotspot.HookCall;
import de.fct.fdmm.protocoldl.HookProtocolState;
import de.fct.fdmm.protocoldl.MessageType;
import de.fct.fdmm.protocoldl.ProtocolHook;

public class UMLTransition implements ProtocolHook {

    private static Logger logger = LoggerFactory.getLogger(UMLTransition.class);

    private Transition transition;
    private MessageType messageType;

    private String hookSignature;

    private HookCall hookCall = null;

    public UMLTransition(Transition transition) {
        this.transition = transition;
        Event event = getTransitionEvent(transition);
        this.messageType = determineMessageType(event);
        this.hookSignature = "";
        if (this.transition.getName() != null && !this.transition.getName().isEmpty()) {
            this.hookSignature = this.transition.getName();
        } else if (this.transition.getLabel() != null && !this.transition.getLabel().isEmpty()) {
            this.hookSignature = this.transition.getLabel();
        } else if (this.transition.getTriggers() != null && !this.transition.getTriggers().isEmpty()) {
            if (this.transition.getTriggers().get(0).getName() != null
                && !this.transition.getTriggers().get(0).getName().isEmpty()) {
                this.hookSignature = this.transition.getTriggers().get(0).getName();
            } else if (this.transition.getTriggers().get(0).getLabel() != null
                       && !this.transition.getTriggers().get(0).getLabel().isEmpty()) {
                this.hookSignature = this.transition.getTriggers().get(0).getLabel();
            } else if (this.transition.getTriggers().get(0).getEvent() != null) {
                if (this.transition.getTriggers().get(0).getEvent().getName() != null
                    && !this.transition.getTriggers().get(0).getEvent().getName().isEmpty()) {
                    this.hookSignature = this.transition.getTriggers().get(0).getEvent().getName();
                } else if (this.transition.getTriggers().get(0).getEvent().getLabel() != null
                           && !this.transition.getTriggers().get(0).getEvent().getLabel().isEmpty()) {
                    this.hookSignature = this.transition.getTriggers().get(0).getEvent().getLabel();
                }
            }
        }
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public HookProtocolState getTarget() {
        Vertex target = this.transition.getTarget();

        UMLState targetState = new UMLState(target);

        return targetState;
    }

    private MessageType determineMessageType(Event event) {
        if (logger.isDebugEnabled()) {
            logger.debug("determineMessageType() from event=" + event);
        }
        MessageType mt = MessageType.UNKNOWN;
        if (event != null) {
            if (event instanceof MessageEvent) {
                mt = MessageType.HOOK;
            } else if (event instanceof CreationEvent) {
                mt = MessageType.CREATION;
            } else if (event instanceof DestructionEvent) {
                mt = MessageType.DESTRUCTION;
            } else {
                logger.warn("determineMessageType() unrecognized event type " + event.getClass().getName());
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("determineMessageType() message type is " + mt);
        }
        return mt;
    }

    private Event getTransitionEvent(Transition transition) {
        Event event = null;
        if (transition.getTriggers() != null) {
            if (transition.getTriggers().size() == 1) {
                Trigger trigger = transition.getTriggers().get(0);
                event = trigger.getEvent();
                if (event == null) {
                    logger.warn("getTransitionEvent() no event specified for trigger " + trigger);
                }
            } else {
                logger.warn("getTransitionEvent() more than one trigger specified for transition "
                            + transition);
            }
        } else {
            logger.warn("getTransitionEvent() no trigger specified for transition " + transition);
        }
        return event;
    }

    public String getHookSignature() {
        return this.hookSignature;
    }

    public HookCall getHookCall() {
        return this.hookCall;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(this.messageType.name());
        if (this.hookSignature != null && !this.hookSignature.isEmpty()) {
            sb.append(" ").append(this.hookSignature);
        }

        return sb.toString();
    }

}
