package de.fct.fdmm.sampledl;

import de.fct.fdmm.protocoldl.HookProtocolState;

public interface HookProtocolElement extends DocElement {

    public HookProtocolState getState();

    public void setState(HookProtocolState state);

}
