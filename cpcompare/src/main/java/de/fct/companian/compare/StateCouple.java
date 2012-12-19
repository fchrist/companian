package de.fct.companian.compare;

import de.fct.fdmm.protocoldl.HookProtocolState;

public class StateCouple {

    private final int hash;
    private final HookProtocolState leftState;
    private final HookProtocolState rightState;

    StateCouple(HookProtocolState leftState, HookProtocolState rightState) {
        this.leftState = leftState;
        this.rightState = rightState;
        this.hash = (leftState.hashCode() << 16) ^ rightState.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof StateCouple)) {
            StateCouple comparedCouple = (StateCouple) obj;
            return this.leftState.equals(comparedCouple.leftState)
                   && this.rightState.equals(comparedCouple.rightState);
        } else {
            return false;
        }
    }

    public HookProtocolState getLeftState() {
        return this.leftState;
    }

    public HookProtocolState getRightState() {
        return this.rightState;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }
}
